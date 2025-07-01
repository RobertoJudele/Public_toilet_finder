package com.toiletfinder.app.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log // Added for debugging
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.* // Added for ModalBottomSheet
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.toiletfinder.app.data.model.Toilet
import com.toiletfinder.app.viewmodel.MapViewModel
import kotlinx.coroutines.Dispatchers // Import Dispatchers for threading
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // Import withContext for threading
import com.toiletfinder.app.R // Import your R class to access drawable resources

/**
 * Composable function for displaying the map screen with toilet locations.
 *
 * @param viewModel The MapViewModel instance to manage map-related data and logic.
 */
@OptIn(ExperimentalMaterial3Api::class) // Required for rememberModalBottomSheetState
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    // Get the current context for accessing resources like Glide
    val context = LocalContext.current

    // State: toilets loaded from Firestore. Collects updates from the ViewModel's StateFlow.
    val toilets by viewModel.toilets.collectAsState()

    // State: loading status from the ViewModel. Collects updates from the ViewModel's StateFlow.
    val isLoading by viewModel.isLoading.collectAsState()

    // State to track if location permissions have been granted
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher for requesting location permissions
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!hasLocationPermission) {
            Log.w("MapScreen", "Location permissions denied by user.")
            // Optionally show a message to the user that location features are limited
        }
    }

    // State for managing the toilet details bottom sheet
    var selectedToilet by remember { mutableStateOf<Toilet?>(null) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false // Allow partially expanded state
    )
    val scope = rememberCoroutineScope() // Coroutine scope for launching sheet operations

    // Define the initial camera position for the map (defaulting to Bucharest)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.Builder()
            .target(LatLng(44.4268, 26.1025)) // Bucharest coordinates
            .zoom(12f) // Initial zoom level
            .build()
    }

    // LaunchedEffect to trigger data loading and permission request when the screen first appears.
    LaunchedEffect(Unit) {
        // Request location permissions when the screen opens
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        viewModel.loadToilets() // Load toilets from Firestore
    }

    // Box layout to layer the map and the loading indicator
    Box(modifier = Modifier.fillMaxSize()) {
        // GoogleMap composable to display the map
        GoogleMap(
            modifier = Modifier.fillMaxSize(), // Fill the entire available space
            cameraPositionState = cameraPositionState, // Control the camera
            uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = true), // Enable zoom controls and my location button
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission), // Enable blue dot if permission granted
            onMapClick = {
                // Dismiss the sheet if the map is clicked (outside a marker)
                if (sheetState.isVisible) {
                    scope.launch { sheetState.hide() }
                    selectedToilet = null
                }
            }
        ) {
            Log.d("MapScreen", "Number of toilets to display: ${toilets.size}")
            toilets.forEach { toilet ->
                Log.d("MapScreen", "Adding marker for: ${toilet.name} at ${toilet.location.latitude}, ${toilet.location.longitude}")
                val latLng = LatLng(toilet.location.latitude, toilet.location.longitude)

                // MarkerInfoWindow to display a marker with an info window
                MarkerInfoWindow(
                    state = rememberMarkerState(position = latLng), // Position of the marker
                    title = toilet.name, // Title of the info window
                    snippet = "Rating: %.1f | %s".format( // Snippet (subtitle) of the info window
                        toilet.rating,
                        if (toilet.accessible) "Accessible" else "Not accessible"
                    ),
                    icon = loadMarkerIcon(context, toilet), // Custom icon for the marker
                    onInfoWindowClick = {
                        // When info window is clicked, set selected toilet and show bottom sheet
                        selectedToilet = toilet
                        scope.launch { sheetState.show() }
                        true // Consume the event
                    },
                    onClick = {
                        // When marker is clicked, set selected toilet and show bottom sheet
                        selectedToilet = toilet
                        scope.launch { sheetState.show() }
                        true // Consume the event
                    }
                ) {
                    // Content of the custom info window (can be simpler now as details are in sheet)
                    Text("${toilet.name}\nRating: %.1f".format(toilet.rating))
                }
            }
        }

        // Show a circular progress indicator if data is currently loading
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
    }

    // Display the ModalBottomSheet if a toilet is selected
    if (selectedToilet != null) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }
                selectedToilet = null // Clear selected toilet on dismiss
            },
            sheetState = sheetState,
        ) {
            selectedToilet?.let { toilet ->
                ToiletDetailsSheet(
                    toilet = toilet,
                    onDismissRequest = {
                        scope.launch { sheetState.hide() }
                        selectedToilet = null
                    }
                )
            }
        }
    }
}

/**
 * Composable function to load and create a custom BitmapDescriptor for map markers.
 * Uses Glide to load an image from a URL and converts it into a BitmapDescriptor.
 *
 * @param context The Android context.
 * @param toilet The Toilet object (used for its ID as a LaunchedEffect key).
 * @return A BitmapDescriptor for the marker icon, or null if loading fails.
 */
@Composable
fun loadMarkerIcon(context: Context, toilet: Toilet): BitmapDescriptor? {
    // State to hold the loaded BitmapDescriptor
    var bitmapDescriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }

    // LaunchedEffect to perform image loading in a coroutine.
    // The key 'toilet.id' ensures that the image is reloaded only if the toilet ID changes.
    LaunchedEffect(toilet.id) {
        val iconSize = 120 // Desired icon size in pixels
        // Original URL loading (commented out as per request)
        // val url = "https://img.icons8.com/color/96/toilet.png"

        try {
            // CRITICAL FIX: Use withContext(Dispatchers.IO) to perform the blocking Glide.get() call on a background thread.
            val bitmap = withContext(Dispatchers.IO) { // Launched on IO dispatcher
                Glide.with(context)
                    .asBitmap()
                    // Load the image from your local drawable resource
                    .load(R.drawable.public_toilet) // Changed to load from local drawable
                    .submit(iconSize, iconSize) // Specify target size for efficient loading
                    .get() // This blocking call is now on a background thread
            }

            // Creating Bitmap and BitmapDescriptor can still be done on the main thread if needed,
            // but it's often fine to keep it within the coroutine scope here.
            val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(outputBitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, null)

            // Convert the bitmap to a BitmapDescriptor for use with Google Maps markers
            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(outputBitmap)
            Log.d("MarkerIcon", "Successfully loaded icon for ${toilet.name} from drawable.")
        } catch (e: Exception) {
            Log.e("MarkerIcon", "Error loading icon for ${toilet.name} from drawable: ${e.message}", e)
            e.printStackTrace()
            // Fallback to default marker if custom icon loading fails
            bitmapDescriptor = BitmapDescriptorFactory.defaultMarker()
        }
    }

    return bitmapDescriptor
}
