package videotrade.parkingProj.presentation.components.Map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

//import parkingproj.composeapp.generated.resources.pin_green
//import parkingproj.composeapp.generated.resources.pin_red
//import parkingproj.composeapp.generated.resources.pin_yellow
import ru.sulgik.mapkit.compose.YandexMap
import ru.sulgik.mapkit.compose.imageProvider
import ru.sulgik.mapkit.compose.rememberYandexMapController
import ru.sulgik.mapkit.geometry.Point
import ru.sulgik.mapkit.map.CameraListener
import ru.sulgik.mapkit.map.CameraPosition
import ru.sulgik.mapkit.map.CameraUpdateReason
import ru.sulgik.mapkit.map.IconStyle
import ru.sulgik.mapkit.map.Map
import ru.sulgik.mapkit.map.PlacemarkMapObject
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.parking_mark
import videotrade.parkingProj.domain.model.MapObjectType
import videotrade.parkingProj.domain.model.MapObjectUserData


@Composable
fun MapUi() {
    val mapController = rememberYandexMapController()

    val placemarks = listOf(
        Point(55.751244, 37.618423) to MapObjectUserData("Красная площадь", MapObjectType.RED),
        Point(55.752244, 37.617423) to MapObjectUserData("ГУМ", MapObjectType.GREEN),
        Point(55.750244, 37.619423) to MapObjectUserData("Мавзолей", MapObjectType.YELLOW),
    )

    val pinRedImage = imageProvider(Res.drawable.parking_mark)
    val pinGreenImage = imageProvider(Res.drawable.parking_mark)
    val pinYellowImage = imageProvider(Res.drawable.parking_mark)

    val typeToImageMap = mapOf(
        MapObjectType.RED to pinRedImage,
        MapObjectType.GREEN to pinGreenImage,
        MapObjectType.YELLOW to pinYellowImage,
    )

    val placemarkRefs = remember { mutableStateListOf<PlacemarkMapObject>() }

    LaunchedEffect(mapController.mapWindow) {
        mapController.mapWindow?.let { mapWindow ->
            val mapObjects = mapWindow.map.mapObjects

            // Добавляем placemarks с начальным масштабом
            placemarks.forEach { (point, data) ->
                val icon = typeToImageMap[data.type]!!
                val placemark = mapObjects.addPlacemark().apply {
                    geometry = point
                    userData = data
                    setIcon(
                        icon,
                        IconStyle(scale = calculateScale(mapWindow.map.cameraPosition.zoom))
                    )
                }
                placemarkRefs += placemark
            }


            // Подписка на изменение зума

            mapWindow.map.addCameraListener(object : CameraListener() {
                override fun onCameraPositionChanged(
                    map: Map,
                    cameraPosition: CameraPosition,
                    cameraUpdateReason: CameraUpdateReason,
                    finished: Boolean
                ) {
                    val zoom = cameraPosition.zoom
                    val scale = calculateScale(zoom)

                    placemarkRefs.forEach { placemark ->
                        val userData = placemark.userData as? MapObjectUserData ?: return@forEach
                        val icon = typeToImageMap[userData.type] ?: return@forEach

                        placemark.setIcon(
                            icon,
                            IconStyle(scale = scale)
                        )
                    }
                }
            })




        }
    }

    Box(Modifier.fillMaxSize()) {
        YandexMap(
            modifier = Modifier.fillMaxSize(),
            controller = mapController,
        )
    }
}

// Примерная функция для масштаба
fun calculateScale(zoom: Float): Float {
    return when {
        zoom > 17f -> 1.3f
        zoom > 15f -> 1.0f
        zoom > 13f -> 0.8f
        zoom > 11f -> 0.6f
        else -> 0.4f
    }
}

