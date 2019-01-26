package pl.temomuko.autostoprace.domain.repository

import pl.temomuko.autostoprace.domain.model.LocationRecord
import pl.temomuko.autostoprace.data.remote.api.Asr2019Service
import pl.temomuko.autostoprace.data.remote.api.model.CreateLocationRequest
import pl.temomuko.autostoprace.data.remote.api.model.LocationEntity
import rx.Completable
import rx.Single
import javax.inject.Inject

class LocationsRepository @Inject constructor(
    private val asr2019Service: Asr2019Service
) {

    fun postLocation(locationRecord: LocationRecord): Single<LocationEntity> {
        val createLocationRequest =
            CreateLocationRequest(
                latitude = locationRecord.latitude,
                longitude = locationRecord.longitude,
                message = locationRecord.message
            )
        return asr2019Service.addLocation(createLocationRequest)
    }

    fun addImageToLocation(locationId: Int): Completable {
        return asr2019Service.addLocationImage(locationId)
    }

    fun getTeamLocations(teamNumber: Long): Single<List<LocationRecord>> {
        return asr2019Service.getTeamLocations(teamNumber)
            .map { locations -> locations.map { it.toLocationRecord() } }
    }

    fun getUserTeamLocations(): Single<List<LocationRecord>> {
        return asr2019Service.getUserTeamLocations()
            .map { locations -> locations.map { it.toLocationRecord() }}
    }
}