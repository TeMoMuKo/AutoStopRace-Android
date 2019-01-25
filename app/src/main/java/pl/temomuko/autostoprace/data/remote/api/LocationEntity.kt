package pl.temomuko.autostoprace.data.remote.api

data class LocationEntity(
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val countryCode: String?,
    val imageUrl: String?,
    val message: String?,
    val createdAt: String,
    val id: Int
)