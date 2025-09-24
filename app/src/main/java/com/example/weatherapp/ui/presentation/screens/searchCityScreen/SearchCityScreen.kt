package com.example.weatherapp.ui.presentation.screens.searchCityScreen

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.weatherapp.R
import com.example.weatherapp.domain.model.GeoLocation


@Composable
fun SearchCityScreen(
    modifier: Modifier = Modifier,
    onNavigateToWeatherHomeScreen: (GeoLocation) -> Unit,
    viewModel: SearchCityViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    // Clear search text when screen is first composed
    LaunchedEffect(Unit) {
        viewModel.clearSearch()
    }

    SearchCityContent(
        modifier = modifier,
        uiState = uiState,
        onTextChange = viewModel::onSearchTextChanged,
        onSubmitClick = {
            viewModel.onSubmitSearch(onNavigateToWeatherHomeScreen)
        },
        onClearSearch = viewModel::clearSearch,
        onDismissError = viewModel::clearError


    )
}


@Composable
fun SearchCityContent(
    modifier: Modifier = Modifier,
    uiState: SearchCityUiState,
    onTextChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onClearSearch: () -> Unit,
    onDismissError: () -> Unit = {}

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
                value = uiState.searchText,
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
                    if (uiState.searchText.isNotEmpty()) {
                        IconButton(
                            onClick = { onTextChange("")
                                onDismissError()
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
                        onSubmitClick()
                    }
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onSubmitClick,
                enabled = uiState.isSearchButtonEnabled && !uiState.isLoading
            ) {
                Text("Search")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchCityContentPreview() {
    SearchCityContent(
        uiState = SearchCityUiState(),
        onTextChange = {},
        onSubmitClick = {},
        onClearSearch = {}
    )
}