package com.dcns.dailycost.ui.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dcns.dailycost.data.CategoriesScreenMode
import com.dcns.dailycost.data.DestinationArgument
import com.dcns.dailycost.data.datasource.local.LocalCategoryDataProvider
import com.dcns.dailycost.domain.use_case.CategoryUseCases
import com.dcns.dailycost.domain.util.GetCategoryBy
import com.dcns.dailycost.foundation.base.BaseViewModel
import com.dcns.dailycost.foundation.common.SharedData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CategoriesViewModel @Inject constructor(
	private val categoryUseCases: CategoryUseCases,
	private val sharedData: SharedData,
	savedStateHandle: SavedStateHandle
): BaseViewModel<CategoriesState, CategoriesAction>(savedStateHandle, CategoriesState()) {

	private val deliveredCategoriesScreenMode =
		savedStateHandle.getStateFlow<CategoriesScreenMode?>(DestinationArgument.CATEGORIES_SCREEN_MODE, null)
	private val deliveredCategoryId =
		savedStateHandle.getStateFlow(DestinationArgument.CATEGORY_ID, LocalCategoryDataProvider.other.id)

	init {
		viewModelScope.launch {
			categoryUseCases.getLocalCategoryUseCase().collect { categoryList ->
				updateState {
					copy(
						categories = categoryList
					)
				}
			}
		}

		viewModelScope.launch(Dispatchers.IO) {
			deliveredCategoriesScreenMode.filterNotNull().collect { mode ->
				updateState {
					copy(
						screenMode = mode
					)
				}
			}
		}

		viewModelScope.launch {
			deliveredCategoryId.flatMapLatest { id ->
				categoryUseCases.getLocalCategoryUseCase(
					getCategoryBy = GetCategoryBy.ID(id)
				).map { it[0] }
			}.collect { category ->
				updateState {
					copy(
						selectedCategory = category
					)
				}
			}
		}
	}

	override fun onAction(action: CategoriesAction) {
		when (action) {
			is CategoriesAction.ChangeSelectedCategory -> {
				viewModelScope.launch {
					updateState {
						copy(
							selectedCategory = action.category
						)
					}
				}
			}
			CategoriesAction.SendCategory -> {
				viewModelScope.launch {
					sharedData.setCategory(state.value.selectedCategory)
				}
			}
		}
	}
}