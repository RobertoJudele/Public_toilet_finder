package com.toiletfinder.app.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
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
import com.toiletfinder.app.R
import com.toiletfinder.app.data.model.Toilet
import com.toiletfinder.app.viewmodel.MapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    onAddToiletClick: () -> Unit = {} // <-- New callback for FAB
) {
    val context = LocalContext.current
    val toilets by viewModel.toilets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    var selectedToilet by remember { mutableStateOf<Toilet?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.Builder()
            .target(LatLng(44.4268, 26.1025))
            .zoom(12f)
            .build()
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        viewModel.loadToilets()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true
            ),
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            onMapClick = {
                if (sheetState.isVisible) {
                    scope.launch { sheetState.hide() }
                    selectedToilet = null
                }
            }
        ) {
            Log.d("MapScreen", "Number of toilets: ${toilets.size}")
            toilets.forEach { toilet ->
                val latLng = LatLng(toilet.location.latitude, toilet.location.longitude)
                MarkerInfoWindow(
                    state = rememberMarkerState(position = latLng),
                    title = toilet.name,
                    snippet = "Rating: %.1f | %s".format(
                        toilet.rating,
                        if (toilet.accessible) "Accessible" else "Not accessible"
                    ),
                    icon = loadMarkerIcon(context, toilet),
                    onClick = {
                        selectedToilet = toilet
                        scope.launch { sheetState.show() }
                        true
                    },
                    onInfoWindowClick = {
                        selectedToilet = toilet
                        scope.launch { sheetState.show() }
                        true
                    }
                ) {
                    Text("${toilet.name}\nRating: %.1f".format(toilet.rating))
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // 🚀 Floating Action Button to Add Toilet
        FloatingActionButton(
            onClick = onAddToiletClick,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Toilet")
        }
    }

    if (selectedToilet != null) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }
                selectedToilet = null
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

@Composable
fun loadMarkerIcon(context: Context, toilet: Toilet): BitmapDescriptor? {
    var bitmapDescriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }

    LaunchedEffect(toilet.id) {
        val iconSize = 120
        try {
            val bitmap = withContext(Dispatchers.IO) {
                Glide.with(context)
                    .asBitmap()
                    .load(R.drawable.public_toilet)
                    .submit(iconSize, iconSize)
                    .get()
            }
            val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(outputBitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(outputBitmap)
        } catch (e: Exception) {
            Log.e("MarkerIcon", "Failed to load icon: ${e.message}", e)
            bitmapDescriptor = BitmapDescriptorFactory.defaultMarker()
        }
    }

    return bitmapDescriptor
}
