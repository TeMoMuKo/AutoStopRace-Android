package pl.temomuko.autostoprace.data.remote.api.repository

import pl.temomuko.autostoprace.data.model.LocationRecord
import pl.temomuko.autostoprace.data.remote.api.Asr2019Service
import pl.temomuko.autostoprace.data.remote.api.CreateLocationRequest
import pl.temomuko.autostoprace.data.remote.api.LocationEntity
import rx.Completable
import rx.Single
import javax.inject.Inject

class LocationsRepository @Inject constructor(
    private val asr2019Service: Asr2019Service
) {

    fun postLocation(
        latitude: Double,
        longitude: Double,
        message: String?
    ): Completable {
        val createLocationRequest = CreateLocationRequest(latitude, longitude, message)
        return asr2019Service.addLocation(createLocationRequest)
    }

    fun addImageToLocation(locationId: Int): Completable {
        return asr2019Service.addLocationImage(locationId)
    }

    fun getTeamLocations(teamNumber: Int): Single<List<LocationRecord>> {
        return asr2019Service.getTeamLocations(teamNumber)
            .map { locations -> locations.map { it.toLocationRecord() } }
    }
}