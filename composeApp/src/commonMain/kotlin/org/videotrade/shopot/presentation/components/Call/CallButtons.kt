package org.videotrade.shopot.presentation.components.Call

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.acceptCall
import shopot.composeapp.generated.resources.microfon
import shopot.composeapp.generated.resources.rejectCall
import shopot.composeapp.generated.resources.svgviewer_png_output

@Composable
fun rejectBtn(onClick: () -> Unit, text: String= "Отменить") {
    Column {
        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier.size(80.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(255, 255, 255))
        ) {
            Image(
                painter = painterResource(Res.drawable.rejectCall),
                alignment = Alignment.Center,
                contentDescription = "Reject Call",
                modifier = Modifier.size(44.dp)
            )
        }
        Text(
            modifier = Modifier.padding(top = 25.dp),
            text = text,
            fontSize = 16.sp,
            color = Color(255, 255, 255)
        )
    }
}

@Composable
fun aceptBtn(onClick: () -> Unit) {
    Column {
        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier.size(80.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(41, 48, 60))
        ) {
            Image(
                painter = painterResource(Res.drawable.acceptCall),
                alignment = Alignment.Center,
                contentDescription = "Accept Call",
                modifier = Modifier.size(44.dp)
            )
        }
        Text(
            modifier = Modifier.padding(top = 25.dp).align(Alignment.CenterHorizontally),
            text = "Принять",
            fontSize = 16.sp,
            color = Color(255, 255, 255)
        )
    }
}

@Composable
fun speakerBtn(onClick: () -> Unit) {
    Column {
        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier.size(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(41, 48, 60))
        ) {
            Image(
                painter = painterResource(Res.drawable.svgviewer_png_output),
                alignment = Alignment.Center,
                contentDescription = "Speaker",
                modifier = Modifier.size(60.dp)
            )
        }
        Text(
            modifier = Modifier.padding(top = 25.dp).align(Alignment.CenterHorizontally),
            text = "Динамик",
            fontSize = 14.sp,
            color = Color(255, 255, 255)
        )
    }
}

@Composable
fun microfonBtn(onClick: () -> Unit) {
    Column {
        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier.size(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(41, 48, 60))
        ) {
            Image(
                painter = painterResource(Res.drawable.microfon),
                alignment = Alignment.Center,
                contentDescription = "Microfon",
                modifier = Modifier.size(60.dp)
            )
        }
        Text(
            modifier = Modifier.padding(top = 25.dp).align(Alignment.CenterHorizontally),
            text = "Микрофон",
            fontSize = 14.sp,
            color = Color(255, 255, 255)
        )
    }
}