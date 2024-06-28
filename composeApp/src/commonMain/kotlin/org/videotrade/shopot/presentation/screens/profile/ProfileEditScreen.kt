package org.videotrade.shopot.presentation.screens.profile


import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.ProfileComponents.GroupEditHeader
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.add_photo
import shopot.composeapp.generated.resources.arrowleft
import shopot.composeapp.generated.resources.delete_account

data class ProfileTextState(
    var firstName: String = "",
    var lastName: String = "",
    var status: String = ""
)

class ProfileEditScreen : Screen {
    
    @Composable
    override fun Content() {
        val mainViewModel: MainViewModel = koinInject()
        val profileViewModel: ProfileViewModel = koinInject()
        val commonViewModel: CommonViewModel = koinInject()
        val profile = mainViewModel.profile.collectAsState(initial = ProfileDTO()).value
        
        val navigator = LocalNavigator.currentOrThrow
        val textState = remember { mutableStateOf(profile.copy()) }
        val textStyle = TextStyle(
            color = Color.Black,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
            lineHeight = 20.sp,
        )
        
        val scope = rememberCoroutineScope()
        val byteArray = remember { mutableStateOf<ByteArray?>(null) }
        var images by remember { mutableStateOf<ImageBitmap?>(null) }
        
        val singleImagePicker = rememberImagePickerLauncher(
            selectionMode = SelectionMode.Single,
            scope = scope,
            onResult = { byteArrays ->
                byteArrays.firstOrNull()?.let {
                    
                    images = it.toImageBitmap()
                    
                    byteArray.value = it
                }
            }
        )
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.87F),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomEnd = 46.dp, bottomStart = 46.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(16.dp)
                ) {
                    GroupEditHeader("Изменить") {
                        scope.launch {
                            val profileUpdate = profileViewModel.sendNewProfile(
                                textState.value,
                                byteArray
                            )
                            
                            if (profileUpdate) {
                                mainViewModel.downloadProfile()
                                navigator.push(ProfileScreen())
                            }
                            
                        }
                    }
                    
                    
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (images !== null) {
                            Surface(
                                modifier = Modifier.size(70.dp),
                                shape = CircleShape,
                            ) {
                                Image(
                                    bitmap = images!!,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(70.dp),
                                )
                            }
                        } else {
                            Avatar(icon = profile.icon, size = 70.dp)
                        }
                        
                        println("profile.icon ${profile.icon}")
                        Column(
                        
                        ) {
                            BasicTextField(
                                value = textState.value.firstName,
                                onValueChange = { newText ->
                                    textState.value = textState.value.copy(firstName = newText)
                                },
                                singleLine = true,
                                textStyle = textStyle,
                                cursorBrush = SolidColor(Color.Black),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                                    .padding(start = 23.dp, bottom = 15.dp),
                                decorationBox = { innerTextField ->
                                    
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .background(Color.Transparent)
                                                .padding(bottom = 4.dp)
                                        ) {
                                            
                                            if (textState.value.firstName.isEmpty()) {
                                                Text(
                                                    "Имя",
                                                    style = textStyle.copy(color = Color.Gray)
                                                )
                                            }
                                            innerTextField()
                                        }
                                        Divider(
                                            color = Color(0xFF8E8E93),
                                            thickness = 1.dp,
                                            modifier = Modifier.width(274.dp)
                                        )
                                    }
                                }
                            
                            )
                            BasicTextField(
                                value = textState.value.lastName,
                                onValueChange = { newText ->
                                    textState.value = textState.value.copy(lastName = newText)
                                },
                                singleLine = true,
                                textStyle = textStyle,
                                cursorBrush = SolidColor(Color.Black),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                                    .padding(start = 23.dp, top = 10.dp),
                                decorationBox = { innerTextField ->
                                    
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .background(Color.Transparent)
                                                .padding(0.dp, bottom = 4.dp)
                                        ) {
                                            
                                            if (textState.value.lastName.isEmpty()) {
                                                Text(
                                                    "Фамилия",
                                                    style = textStyle.copy(color = Color.Gray)
                                                )
                                            }
                                            innerTextField()
                                        }
                                        Divider(
                                            color = Color(0xFF8E8E93),
                                            thickness = 1.dp,
                                            modifier = Modifier.width(274.dp)
                                        )
                                    }
                                }
                            
                            )
                        }
                    }
                    
                    
                    Box(
                        modifier = Modifier
                            .padding(top = 25.dp, bottom = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                singleImagePicker.launch()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.add_photo),
                                contentDescription = "Avatar",
                                modifier = Modifier.size(width = 27.75.dp, height = 20.dp),
                                contentScale = ContentScale.FillBounds
                            )
                            Text(
                                "Загрузить фотографию",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 15.sp,
                                color = Color(0xFF2A293C),
                                modifier = Modifier.padding(start = 10.dp)
                            )
                        }
                    }
                    
                    
                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BasicTextField(
                            value = "${textState.value.status}",
                            onValueChange = { newText ->
                                textState.value = textState.value.copy(status = newText)
                            },
                            singleLine = true,
                            textStyle = textStyle.copy(textAlign = TextAlign.Center),
                            cursorBrush = SolidColor(Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                                .padding(start = 0.dp, top = 10.dp),
                            decorationBox = { innerTextField ->
                                
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.Transparent)
                                            .padding(0.dp, bottom = 4.dp),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        
                                        if (textState.value.status?.isEmpty() == true) {
                                            Text(
                                                "Статус",
                                                style = textStyle.copy(color = Color.Gray)
                                            )
                                        }
                                        innerTextField()
                                    }
                                    Divider(
                                        color = Color(0xFF8E8E93),
                                        thickness = 1.dp,
                                        modifier = Modifier.width(274.dp)
                                    )
                                }
                            }
                        
                        )
                    }
                    Spacer(modifier = Modifier.height(42.dp))
                    
                }
                
                Box(
                    modifier = Modifier
                        .padding(top = 0.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF000000))
                        .fillMaxWidth(0.9F)
                        .padding(start = 15.dp, top = 14.dp, end = 10.dp, bottom = 14.dp)
                        .clickable {
                            commonViewModel.mainNavigator.value?.let { mainViewModel.leaveApp(it) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(end = 18.dp)
                                    .size(width = 39.dp, height = 25.dp),
                                painter = painterResource(Res.drawable.delete_account),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds
                            )
                            Text(
                                "Удалить аккаунт",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 15.sp,
                                color = Color(0xFFFF0000)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            
                            Image(
                                modifier = Modifier
                                    .size(18.dp).padding(top = 5.dp),
                                painter = painterResource(Res.drawable.arrowleft),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}


