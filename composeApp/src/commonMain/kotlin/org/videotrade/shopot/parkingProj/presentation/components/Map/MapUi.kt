package videotrade.parkingProj.presentation.components.Map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

//import parkingproj.composeapp.generated.resources.pin_green
//import parkingproj.composeapp.generated.resources.pin_red
//import parkingproj.composeapp.generated.resources.pin_yellow
import ru.sulgik.mapkit.compose.YandexMap
import ru.sulgik.mapkit.compose.imageProvider
import ru.sulgik.mapkit.compose.rememberYandexMapController
import ru.sulgik.mapkit.geometry.Point
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

    // Дожидаемся, пока mapWindow станет доступен
    LaunchedEffect(mapController.mapWindow) {
        mapController.mapWindow?.let { mapWindow ->
            val mapObjects = mapWindow.map.mapObjects
            placemarks.forEach { (point, data) ->
                mapObjects.addPlacemark().apply {
                    geometry = point
                    setIcon(typeToImageMap[data.type]!!)
                    userData = data
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        YandexMap(
            modifier = Modifier.fillMaxSize(),
            controller = mapController,
//                cameraPositionState = rememberCameraPositionState {
//                    position = CameraPosition(Point(55.751244, 37.618423), 16f, 0f, 0f)
//                }
        )
    }
}

