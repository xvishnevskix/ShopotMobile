package videotrade.parkingProj.domain.model

enum class MapObjectType {
    YELLOW,
    GREEN,
    RED
}

data class MapObjectUserData(
    val name: String,
    val type: MapObjectType,
)