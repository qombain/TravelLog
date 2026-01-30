package com.example.travellog.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.travellog.model.Trip
import com.example.travellog.viewmodel.TripViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

// Definicja tras (Routes)
object Routes {
    const val HOME = "home"
    const val TRACKING = "tracking"
    const val FULL_IMAGE = "full_image/{photoUri}" // Nowa trasa z parametrem
}

@Composable
fun TravelLogApp() {
    val navController = rememberNavController()
    val viewModel: TripViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(navController, viewModel)
        }
        composable(Routes.TRACKING) {
            TrackingScreen(navController, viewModel)
        }
        // Obsługa ekranu pełnego zdjęcia
        composable(
            route = Routes.FULL_IMAGE,
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("photoUri")
            val decodedUri = encodedUri?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            }
            if (decodedUri != null) {
                FullScreenImageScreen(navController, decodedUri)
            }
        }
    }
}

// --- EKRAN GŁÓWNY (HISTORIA) ---
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: TripViewModel) {
    val trips by viewModel.allTrips.collectAsState(initial = emptyList())

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.CAMERA
        )
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (permissionsState.allPermissionsGranted) {
                    navController.navigate(Routes.TRACKING)
                } else {
                    permissionsState.launchMultiplePermissionRequest()
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nowa wycieczka")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // >>> NOWY NAGŁÓWEK <<<
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Witaj w TravelLog",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Twoje centrum wspomnień",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            HorizontalDivider()

            Text(
                "Moje Podróże",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            if (!permissionsState.allPermissionsGranted) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Text(
                        "Wymagane uprawnienia do działania aplikacji! Kliknij + aby nadać.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (trips.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Brak zarejestrowanych podróży.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn {
                    items(trips) { trip ->
                        TripItem(
                            trip = trip,
                            onDelete = { viewModel.deleteTrip(trip) },
                            onImageClick = { uri ->
                                // Kodujemy URI przed wysłaniem, żeby znaki specjalne nie zepsuły nawigacji
                                val encoded = URLEncoder.encode(uri, StandardCharsets.UTF_8.toString())
                                navController.navigate("full_image/$encoded")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TripItem(trip: Trip, onDelete: () -> Unit, onImageClick: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(trip.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "Data: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(trip.date))}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Usuń", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AssistChip(
                    onClick = {},
                    label = { Text("Kroki: ${trip.steps}") },
                    leadingIcon = { Icon(Icons.Default.DirectionsWalk, null) }
                )
                AssistChip(
                    onClick = {},
                    label = { Text("Dystans: ${"%.1f".format(trip.distance)} m") }
                )
            }

            // Zdjęcie z bazy z obsługą kliknięcia
            trip.photoUri?.let { uriString ->
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = uriString,
                    contentDescription = "Zdjęcie z trasy",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .clickable { onImageClick(uriString) } // >>> KLIKNIĘCIE W ZDJĘCIE <<<
                )
                Text(
                    "Kliknij zdjęcie, aby powiększyć",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// --- EKRAN PEŁNEGO ZDJĘCIA  ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenImageScreen(navController: NavController, photoUri: String) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Podgląd zdjęcia") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                    }
                },
                actions = {
                    // Przycisk Udostępnij / Pobierz
                    IconButton(onClick = {
                        val uri = Uri.parse(photoUri)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/jpeg"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Zapisz lub udostępnij zdjęcie"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Udostępnij")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Pełne zdjęcie",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

// --- EKRAN ŚLEDZENIA (TRACKING) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(navController: NavController, viewModel: TripViewModel) {
    val steps by viewModel.currentSteps.collectAsState()
    val distance by viewModel.currentDistance.collectAsState()
    val context = LocalContext.current
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var hasPhoto by remember { mutableStateOf(false) }

    fun createImageFile(): File {
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempPhotoUri != null) {
            viewModel.setPhoto(tempPhotoUri.toString())
            hasPhoto = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.startTracking()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Trwa wycieczka...") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.DirectionsWalk, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            Text("$steps", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
            Text("KROKÓW", style = MaterialTheme.typography.labelLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Text("${"%.1f".format(distance)} m", style = MaterialTheme.typography.displayMedium)
            Text("DYSTANS", style = MaterialTheme.typography.labelLarge)

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    val file = createImageFile()
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    tempPhotoUri = uri
                    cameraLauncher.launch(uri)
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (hasPhoto) "Zmień zdjęcie" else "Zrób zdjęcie")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.stopTracking("Wycieczka ${SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date())}")
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Zakończ i Zapisz")
            }
        }
    }
}