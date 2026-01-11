package com.example.weatherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.ui.CityDetailViewModelFactory
import com.example.weatherapp.ui.ViewModelFactory
import com.example.weatherapp.ui.citydetail.CityDetailScreen
import com.example.weatherapp.ui.citydetail.CityDetailViewModel
import com.example.weatherapp.ui.citylist.CityListScreen
import com.example.weatherapp.ui.citylist.CityListViewModel

sealed class Screen(val route: String) {
    object CityList : Screen("city_list")
    object CityDetail : Screen("city_detail/{cityId}") {
        fun createRoute(cityId: Int) = "city_detail/$cityId"
    }
}

@Composable
fun WeatherNavigation(
    database: WeatherDatabase,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val repository = WeatherRepositoryImpl(
        cityDao = database.cityDao(),
        forecastDao = database.forecastDao()
    )

    NavHost(
        navController = navController,
        startDestination = Screen.CityList.route,
        modifier = modifier
    ) {
        composable(Screen.CityList.route) {
            val viewModel: CityListViewModel = viewModel(
                factory = ViewModelFactory(repository)
            )

            CityListScreen(
                viewModel = viewModel,
                onCityClick = { cityId ->
                    navController.navigate(Screen.CityDetail.createRoute(cityId))
                }
            )
        }

        composable(
            route = Screen.CityDetail.route,
            arguments = listOf(
                navArgument("cityId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val cityId = backStackEntry.arguments?.getInt("cityId") ?: return@composable

            val viewModel: CityDetailViewModel = viewModel(
                factory = CityDetailViewModelFactory(repository, cityId)
            )

            CityDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}
