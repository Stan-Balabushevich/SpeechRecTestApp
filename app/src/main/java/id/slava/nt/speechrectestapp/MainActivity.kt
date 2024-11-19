package id.slava.nt.speechrectestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import id.slava.nt.speechrectestapp.mainscreen.SpeechRecognitionScreen
import id.slava.nt.speechrectestapp.ui.theme.SpeechRecTestAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpeechRecTestAppTheme {
               SpeechRecognitionScreen()
            }
        }
    }
}
