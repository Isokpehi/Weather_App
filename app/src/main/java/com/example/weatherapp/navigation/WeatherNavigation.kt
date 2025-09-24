package com.example.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.weatherapp.domain.model.GeoLocation
import com.example.weatherapp.ui.presentation.screens.searchCityScreen.SearchCityScreen
import com.example.weatherapp.ui.presentation.screens.splashScreen.SplashContent
import com.example.weatherapp.ui.presentation.screens.weatherHomeScreen.WeatherHomeScreen

@Composable
fun WeatherNavigation(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = WeatherRoutes.SplashScreen) {

        composable<WeatherRoutes.SplashScreen> {
            SplashContent(
                onNavigateToSearchCityScreen = {
                    navController.navigate(WeatherRoutes.SearchCityScreen) {
                        // Remove splash from back stack
                        popUpTo(WeatherRoutes.SplashScreen) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<WeatherRoutes.SearchCityScreen> {
            SearchCityScreen(
                onNavigateToWeatherHomeScreen = { geoLocation ->
                    navController.navigate(
                        WeatherRoutes.WeatherHomeScreen(
                            geoLocation.name,
                            geoLocation.lat,
                            geoLocation.lon,
                            geoLocation.country,
                            geoLocation.state
                        )
                    )
                }
            )
        }

        composable<WeatherRoutes.WeatherHomeScreen> { backStackEntry ->
            val weatherRoute = backStackEntry.toRoute<WeatherRoutes.WeatherHomeScreen>()

            // Reconstruct GeoLocation from the individual fields
            val geoLocation = GeoLocation(
                name = weatherRoute.cityName,
                lat = weatherRoute.latitude,
                lon = weatherRoute.longitude,
                country = weatherRoute.country,
                state = weatherRoute.state
            )

            WeatherHomeScreen(
                geoLocation = geoLocation,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}