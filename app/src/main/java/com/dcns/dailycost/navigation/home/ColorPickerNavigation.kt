package com.dcns.dailycost.navigation.home

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import com.dcns.dailycost.data.NavigationActions
import com.dcns.dailycost.data.TopLevelDestinations
import com.dcns.dailycost.ui.color_picker.ColorPickerScreen
import com.dcns.dailycost.ui.color_picker.ColorPickerViewModel

fun NavGraphBuilder.ColorPickerNavigation(navigationActions: NavigationActions) {
	dialog(
		route = TopLevelDestinations.Home.colorPicker.route
	) { backEntry ->
		val viewModel = hiltViewModel<ColorPickerViewModel>(backEntry)

		ColorPickerScreen(
			viewModel = viewModel,
			navigationActions = navigationActions
		)
	}
}
