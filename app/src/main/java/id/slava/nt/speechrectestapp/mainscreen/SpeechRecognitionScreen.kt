package id.slava.nt.speechrectestapp.mainscreen

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import id.slava.nt.speechrectestapp.utils.PermissionsHandler
import id.slava.nt.speechrectestapp.utils.SpeechRecognitionHelper
import kotlinx.coroutines.delay

@Composable
fun SpeechRecognitionScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Use `remember` to create instances once per composition
    val speechRecognitionHelper =
        remember { SpeechRecognitionHelper(context, "ru-RU") }
    val permissionHandler = remember { PermissionsHandler(context) }
    var isListening by remember { mutableStateOf(false) } // New state to track if listening
    var startTime by remember { mutableLongStateOf(0) }

    // State variables
    var userText by remember { mutableStateOf("Ваше сообщение") }
    var showDialog by remember { mutableStateOf(false) }
    var permissionsGranted by remember { mutableStateOf(false) }

    // Permission dialog screen
    PermissionDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        permissionName = "Аудиозапись"
    )

    DisposableEffect(Unit) {
        onDispose {
            speechRecognitionHelper.destroy()
        }
    }

    // Observer for lifecycle changes to mimic "resume" behavior
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionHandler.setPermissionCallbacks(
                    onGranted = { permission ->
                        if (permission == Manifest.permission.RECORD_AUDIO) {
                            speechRecognitionHelper.startRecognition(
                                onTextUpdate = {
                                    userText = it
                                },
                                onErrorMessage = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                },
                                onListeningStateChange = { isListening = it })
                            permissionsGranted = true
                        }
                    },
                    onDenied = { permission ->
                        if (permission == Manifest.permission.RECORD_AUDIO) {
                            showDialog = true
                        }
                    }
                )
                permissionHandler.requestPermission(Manifest.permission.RECORD_AUDIO)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Update the text with elapsed time while listening
    LaunchedEffect(isListening) {
        if (isListening) {
            startTime = System.currentTimeMillis() // Record the start time
            while (isListening) {
                val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
                userText = "Слушаю... ${elapsedSeconds}s"
                delay(1000L)  // Update the text every second
            }
        }
    }

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle FAB click */ }) {
                MicrophoneButton(speechRecognitionHelper)
            }

        },
        content = { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(vertical = 22.dp, horizontal = 16.dp),

                ) {

                if (permissionsGranted) {

                    Text( text = userText )

                } else {
                    Text(text = "Разрешения не предоставлены")
                    Button(onClick = {
                        permissionHandler.requestPermission(Manifest.permission.RECORD_AUDIO)
                    }) {
                        Text(text = "Запросить разрешения")
                    }
                }
            }
        }
    )
}

