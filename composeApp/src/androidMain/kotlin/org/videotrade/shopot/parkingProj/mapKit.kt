package org.videotrade.shopot.parkingProj
import android.content.Context
import ru.sulgik.mapkit.MapKit

fun mapKitInit (context: Context) {
    initMapKit()

    com.yandex.mapkit.MapKitFactory.initialize(context)
}

fun initMapKit() {
    MapKit.setApiKey("20dfed5a-7111-4232-a087-689608e1ea43")
}