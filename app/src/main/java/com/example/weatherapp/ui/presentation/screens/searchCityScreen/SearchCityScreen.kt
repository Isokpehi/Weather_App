package com.example.weatherapp.ui.presentation.screens.searchCityScreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.weatherapp.R
import com.example.weatherapp.domain.model.GeoLocation


/**
 * Search City Screen - Main entry point for the city search functionality.
 *
 * Features:
 * - City search with API integration
 * - Loading states during search
 * - Error handling via Toast messages
 * - Saved city functionality (pre-populates search field)
 * - Input validation and keyboard actions
 *
 * @param modifier Modifier for the root composable
 * @param onNavigateToWeatherHomeScreen Callback to navigate to weather details screen
 * @param viewModel ViewModel handling search logic and state
 * @param savedCityViewModel ViewModel handling saved city data
 */
@Composable
fun SearchCityScreen(
    modifier: Modifier = Modifier,
    onNavigateToWeatherHomeScreen: (GeoLocation) -> Unit,
    viewModel: SearchCityViewModel = hiltViewModel(),
    savedCityViewModel: SavedCityViewModel = hiltViewModel()
) {

    // Collect UI state from ViewModels
    val uiState by viewModel.uiState.collectAsState()
    val savedCityState by savedCityViewModel.getData.collectAsState()
    val cityFromStore = savedCityState.city
    val context = LocalContext.current

    // Handle error messages via Toast
    // This LaunchedEffect triggers whenever errorMessage changes
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError() // Clear error after showing toast
        }
    }

    // Clear search text when screen is first composed
    // This ensures fresh state when navigating to this screen
    LaunchedEffect(Unit) {
        viewModel.clearSearch()
    }

// Render the UI content
    SearchCityContent(
        modifier = modifier,
        searchText = cityFromStore ?: uiState.searchText,
        isLoading = uiState.isLoading,
        onTextChange = viewModel::onSearchTextChanged,
        onSubmitClick = {
            // Use saved city if available, otherwise use search text
            val searchQuery = if (cityFromStore?.isNotEmpty() == true) {
                // Extract just the city name from "City, Country" format
                cityFromStore.split(",").firstOrNull()?.trim() ?: cityFromStore
            } else {
                uiState.searchText
            }

            // Update the search text in viewModel if using saved city
            if (cityFromStore?.isNotEmpty() == true && uiState.searchText.isEmpty()) {
                viewModel.onSearchTextChanged(searchQuery)
            }

            viewModel.onSubmitSearch(onNavigateToWeatherHomeScreen)

        },
        onClearSearch = {
            viewModel.clearSearch()
            savedCityViewModel.clearCategory()
        }
    )

}


/**
* Search City Content - UI layout for the search functionality.
*
* Layout structure:
* - App logo and branding
* - Weather icon
* - Search input field with icons
* - Search button with loading state
*
* @param modifier Modifier for styling
* @param searchText Current text in search field
* @param isLoading Whether search operation is in progress
* @param onTextChange Callback when user types in search field
* @param onSubmitClick Callback when search is submitted
* @param onClearSearch Callback to clear search state
*/
@Composable
fun SearchCityContent(
    modifier: Modifier = Modifier,
    searchText: String,
    onTextChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onClearSearch: () -> Unit,
    isLoading: Boolean = false
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // Weather App Logo and Text
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "W",
                    color = Color(0xFFFF0601),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "weather",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = "App",
                        color = Color(0xFF757575),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            // Weather Icon
            Image(
                painter = painterResource(id = R.drawable.ic_sun_cloud_angled_rain), // Replace with your SVG resource
                contentDescription = "Weather Icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(280.dp)
            )

            Spacer(modifier = Modifier.height(44.dp))
            OutlinedTextField(
                singleLine = true,
                value = searchText,
                shape = RoundedCornerShape(20.dp),
                onValueChange = onTextChange,
                label = { Text("Enter City") },
                placeholder = { Text("e.g. Lagos, London, New York") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onTextChange("")
                                onClearSearch()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear text",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!isLoading && searchText.isNotEmpty()) {
                            onSubmitClick()
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onSubmitClick,
                enabled = searchText.isNotEmpty() && !isLoading,
            ) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Searching...")
                    }
                } else {
                    Text("Search")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchCityContentPreview() {
    SearchCityContent(
        onTextChange = {},
        onSubmitClick = {},
        onClearSearch = {},
        searchText = ""
    )
}