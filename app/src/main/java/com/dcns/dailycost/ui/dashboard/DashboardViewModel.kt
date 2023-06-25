package com.dcns.dailycost.ui.dashboard

import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.dcns.dailycost.R
import com.dcns.dailycost.data.model.remote.response.ErrorResponse
import com.dcns.dailycost.data.repository.UserCredentialRepository
import com.dcns.dailycost.domain.use_case.DepoUseCases
import com.dcns.dailycost.domain.use_case.NoteUseCases
import com.dcns.dailycost.domain.util.GetNoteBy
import com.dcns.dailycost.foundation.base.BaseViewModel
import com.dcns.dailycost.foundation.base.UiEvent
import com.dcns.dailycost.foundation.common.ConnectivityManager
import com.dcns.dailycost.foundation.common.SharedUiEvent
import com.dcns.dailycost.foundation.extension.toNote
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userCredentialRepository: UserCredentialRepository,
    private val connectivityManager: ConnectivityManager,
    private val sharedUiEvent: SharedUiEvent,
    private val depoUseCases: DepoUseCases,
    private val noteUseCases: NoteUseCases
): BaseViewModel<DashboardState, DashboardAction>() {

    init {
        viewModelScope.launch {
            sharedUiEvent.uiEvent.filterNotNull().collect { event ->
                when (event) {
                    is DashboardUiEvent.TopUpSuccess -> sendEvent(event)
                }
            }
        }

        viewModelScope.launch {
            connectivityManager.isNetworkAvailable.asFlow().collect { have ->
                Timber.i("have internet: $have")

                updateState {
                    copy(
                        internetConnectionAvailable = have
                    )
                }

                // Kalo ga ada koneksi internet, show snackbar
                if (!have) {
                    sendEvent(DashboardUiEvent.NoInternetConnection())
                }
            }
        }

        viewModelScope.launch {
            noteUseCases.getLocalNoteUseCase().collect { notes ->
                updateState {
                    copy(
                        recentNotes = notes.take(2)
                    )
                }
            }
        }

        viewModelScope.launch {
            depoUseCases.getLocalBalanceUseCase().collect { balance ->
                updateState {
                    copy(
                        balance = balance
                    )
                }
            }
        }

        viewModelScope.launch {
            userCredentialRepository.getUserCredential.collect { cred ->
                updateState {
                    copy(
                        credential = cred
                    )
                }
            }
        }
    }

    private suspend fun getRemoteNote() {
        val mState = state.value

        noteUseCases.getRemoteNoteUseCase(
            token = mState.credential.getAuthToken(),
            getNoteBy = GetNoteBy.UserID(mState.credential.id.toInt())
        ).let { response ->
            if (response.isSuccessful) {
                val noteResponse = response.body()

                noteResponse?.let {
                    Timber.i("upserting notes to db...")
                    withContext(Dispatchers.IO) {
                        noteUseCases.upsertLocalNoteUseCase(
                            *noteResponse.data
                                .map { it.toNote() }
                                .toTypedArray()
                        )
                    }
                }

                Timber.i("get remote note success")

                return
            }

            val errorResponse = Gson().fromJson(
                response.errorBody()?.charStream(),
                ErrorResponse::class.java
            )

            sendEvent(DashboardUiEvent.GetRemoteNoteFailed(errorResponse.message))
        }
    }

    private suspend fun getRemoteBalance() {
        val mState = state.value

        depoUseCases.getRemoteBalanceUseCase(
            token = mState.credential.getAuthToken(),
            userId = mState.credential.id.toInt()
        ).let { response ->
            if (response.isSuccessful) {
                val balanceResponseData = response.body()?.data

                balanceResponseData?.let {
                    depoUseCases.updateLocalBalanceUseCase(
                        cash = balanceResponseData.cash.toDouble(),
                        eWallet = balanceResponseData.eWallet.toDouble(),
                        bankAccount = balanceResponseData.bankAccount.toDouble()
                    )
                }

                Timber.i("get remote balance success")

                return
            }

            val errorResponse = Gson().fromJson(
                response.errorBody()?.charStream(),
                ErrorResponse::class.java
            )

            sendEvent(
                DashboardUiEvent.GetRemoteBalanceFailed(
                    message = if (response.code() == 404) UiEvent.asStringResource(R.string.you_have_no_balance)
                    else errorResponse.message
                )
            )
        }
    }

    override fun defaultState(): DashboardState = DashboardState()

    override fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.Refresh -> viewModelScope.launch {
                // Kalo ga ada koneksi internet, show snackbar
                if (!state.value.internetConnectionAvailable) {
                    sendEvent(DashboardUiEvent.NoInternetConnection())
                    return@launch
                }

                updateState {
                    copy(
                        isRefreshing = true
                    )
                }

                getRemoteNote()
                getRemoteBalance()

                updateState {
                    copy(
                        isRefreshing = false
                    )
                }
            }
        }
    }
}