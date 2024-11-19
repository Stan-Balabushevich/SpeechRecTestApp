package id.slava.nt.speechrectestapp.mainscreen

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PermissionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    permissionName: String
) {

    val context = LocalContext.current

    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Box(
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    Text(text = "Эта функция требует разрешения $permissionName. Хотите открыть настройки телефона и изменить разрешение?")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(text = "Настройки", modifier = Modifier.clickable {
                            val packageName = context.packageName
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.fromParts("package", packageName, null)
                            context.startActivity(intent)
                            onDismiss()
                        })

                        Text(text = "Отмена", modifier = Modifier.clickable {
                            onDismiss()
                        })
                    }
                }
            }
        }
    }
}