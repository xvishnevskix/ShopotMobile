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
import org.videotrade.shopot.api.EnvironmentConfig
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person

@Composable
fun CustomImage(
    icon: String? = null,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier.size(size),
    contentScale: ContentScale = ContentScale.Crop,
) {
    val imagePainter = if (icon == null) {
        painterResource(Res.drawable.person)
    } else {
        rememberImagePainter("${EnvironmentConfig.serverUrl}file/id/$icon")
//        rememberImagePainter("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTE5fPhctwNLodS9VmAniEw_UiLWHgKs0fs1w&s")
    }
    
    
    
    Image(
        painter = imagePainter,
        contentDescription = "Avatar",
        contentScale = contentScale,
        modifier = modifier,
    )
    
    
}
