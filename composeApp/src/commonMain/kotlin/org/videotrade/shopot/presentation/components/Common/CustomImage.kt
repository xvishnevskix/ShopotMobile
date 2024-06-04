package org.videotrade.shopot.presentation.components.Common


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person

@Composable
fun CustomImage(
    imageUri: String? = null,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier.size(size),
    contentScale: ContentScale = ContentScale.Crop,
) {
    val imagePainter = if (imageUri == null) {
        painterResource(Res.drawable.person)
    } else {
        rememberImagePainter(imageUri)
    }
    
    
    
    Image(
        painter = imagePainter,
        contentDescription = "Avatar",
        contentScale = contentScale,
        modifier = modifier,
    )
    
    
}
