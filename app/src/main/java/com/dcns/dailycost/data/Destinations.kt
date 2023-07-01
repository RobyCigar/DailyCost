package com.dcns.dailycost.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.dcns.dailycost.R

object DestinationRoute {
    const val CHANGE_LANGUAGE = "change_language"
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val REGISTER = "register"
    const val SETTING = "setting"
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val NOTES = "notes"
    const val NOTE = "note"
}

/**
 * Key untuk argument
 */
object DestinationArgument {

}

data class TopLevelDestination(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList(),
    @StringRes val name: Int? = null,
    @DrawableRes val icon: Int? = null
) {
    /**
     * @param value {key: value}
     */
    fun createRoute(vararg value: Pair<Any, Any>): TopLevelDestination {
        var mRoute = route

        value.forEach { (key, value) ->
            mRoute = mRoute.replace("{$key}", value.toString())
        }

        return TopLevelDestination(mRoute, arguments)
    }
}

class NavigationActions(private val navController: NavHostController) {

    fun popBackStack() = navController.popBackStack()

    fun popBackStack(
        destinationId: Int,
        inclusive: Boolean = false
    ) = navController.popBackStack(
        destinationId,
        inclusive
    )

    fun popBackStack(
        destinationId: Int,
        inclusive: Boolean = false,
        saveState: Boolean = false
    ) = navController.popBackStack(
        destinationId,
        inclusive,
        saveState
    )

    fun popBackStack(
        route: String,
        inclusive: Boolean = false,
        saveState: Boolean = false
    ) = navController.popBackStack(
        route,
        inclusive,
        saveState
    )

    fun navigateTo(
        destination: TopLevelDestination,
        builder: NavOptionsBuilder.() -> Unit = defaultNavOptionsBuilder()
    ) {
        navController.navigate(destination.route, builder)
    }

    companion object {
        fun defaultNavOptionsBuilder(
            popTo: TopLevelDestination? = null,
            launchAsSingleTop: Boolean = true,
            saveStatePopUpTo: Boolean = true,
            inclusivePopUpTo: Boolean = false,
            restoreAnyState: Boolean = true
        ): NavOptionsBuilder.() -> Unit = {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popTo?.let {
                popUpTo(popTo.route) {
                    saveState = saveStatePopUpTo
                    inclusive = inclusivePopUpTo
                }
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = launchAsSingleTop
            // Restore state when re-selecting a previously selected item
            restoreState = restoreAnyState
        }
    }
}

object TopLevelDestinations {

    object Onboarding {
        const val ROOT_ROUTE = "root_onboarding"

        val onboarding = TopLevelDestination(
            route = DestinationRoute.ONBOARDING
        )
    }

    object LoginRegister {
        const val ROOT_ROUTE = "root_login_register"

        val login = TopLevelDestination(
            route = DestinationRoute.LOGIN
        )

        val register = TopLevelDestination(
            route = DestinationRoute.REGISTER
        )
    }

    object Home {
        const val ROOT_ROUTE = "root_home"

        val changeLanguage = TopLevelDestination(
            route = DestinationRoute.CHANGE_LANGUAGE
        )

        val dashboard = TopLevelDestination(
            route = DestinationRoute.DASHBOARD,
            icon = R.drawable.ic_dashboard,
            name = R.string.dashboard
        )

        val setting = TopLevelDestination(
            route = DestinationRoute.SETTING,
            icon = R.drawable.ic_setting,
            name = R.string.advance_setting
        )

        val notes = TopLevelDestination(
            route = DestinationRoute.NOTES,
            icon = R.drawable.ic_notes,
            name = R.string.notes
        )

        val note = TopLevelDestination(
            route = DestinationRoute.NOTE
        )
    }

    val splash = TopLevelDestination(
        route = DestinationRoute.SPLASH
    )

}

val drawerDestinations = arrayOf(
    TopLevelDestinations.Home.dashboard,
    TopLevelDestinations.Home.notes,
    TopLevelDestinations.Home.setting,
)
