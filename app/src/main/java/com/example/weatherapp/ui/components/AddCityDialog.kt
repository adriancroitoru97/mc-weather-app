package com.example.weatherapp.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices


@Composable
fun AddCityDialog(
    onDismiss: () -> Unit,
    onAddByName: (String) -> Unit,
    onAddByLocation: (Double, Double) -> Unit,
    locationPermissionsGranted: Boolean,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cityName by remember { mutableStateOf("") }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add City",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Add by name section
                OutlinedTextField(
                    value = cityName,
                    onValueChange = { cityName = it },
                    label = { Text("City name") },
                    placeholder = { Text("Enter city name") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = { 
                        if (cityName.isNotBlank()) {
                            onAddByName(cityName)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = cityName.isNotBlank() && !isLoadingLocation
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add by Name")
                }

                HorizontalDivider()

                // Add by location section
                Text(
                    text = "Or use your current location",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = {
                        if (locationPermissionsGranted) {
                            isLoadingLocation = true
                            locationError = null
                            getCurrentLocation(context) { lat, lon, error ->
                                isLoadingLocation = false
                                if (lat != null && lon != null) {
                                    onAddByLocation(lat, lon)
                                } else {
                                    locationError = error ?: "Failed to get location"
                                }
                            }
                        } else {
                            onRequestPermissions()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoadingLocation,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    if (isLoadingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (locationPermissionsGranted) 
                            "Use Current Location" 
                        else 
                            "Grant Location Permission"
                    )
                }

                locationError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier
    )
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    onResult: (Double?, Double?, String?) -> Unit
) {
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        // Create location request
        val locationRequest = com.google.android.gms.location.LocationRequest().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
            numUpdates = 1 // Only need one update
        }
        
        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                val location = locationResult.lastLocation
                if (location != null) {
                    onResult(location.latitude, location.longitude, null)
                } else {
                    onResult(null, null, "Location not available. Please enable location services and try again.")
                }
            }
        }
        
        // First try to get last known location (fast)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null && (System.currentTimeMillis() - location.time < 60000)) {
                    // Use last location if it's less than 1 minute old
                    onResult(location.latitude, location.longitude, null)
                } else {
                    // Request fresh location update
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        android.os.Looper.getMainLooper()
                    )
                    
                    // Set a timeout - if no location after 15 seconds, return error
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                        onResult(null, null, "Location request timed out. Please check your location settings or try again.")
                    }, 15000)
                }
            }
            .addOnFailureListener { e ->
                // If lastLocation fails, try requesting updates
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    android.os.Looper.getMainLooper()
                )
                
                // Set a timeout
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    onResult(null, null, "Error getting location: ${e.message}. Please check your location settings.")
                }, 15000)
            }
    } catch (e: Exception) {
        onResult(null, null, "Error: ${e.message}")
    }
}
