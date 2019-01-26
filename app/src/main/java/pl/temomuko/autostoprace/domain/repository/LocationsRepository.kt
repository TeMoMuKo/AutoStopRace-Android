package pl.temomuko.autostoprace.domain.repository

import pl.temomuko.autostoprace.domain.model.LocationRecord
import pl.temomuko.autostoprace.data.remote.AsrService
import pl.temomuko.autostoprace.data.remote.model.CreateLocationRequest
import pl.temomuko.autostoprace.data.remote.model.LocationEntity
import rx.Completable
import rx.Single
import javax.inject.Inject

class LocationsRepository @Inject constructor(
    private val asrService: AsrService
) {

    fun postLocation(locationRecord: LocationRecord): Single<LocationEntity> {
        val createLocationRequest =
            CreateLocationRequest(
                latitude = locationRecord.latitude,
                longitude = locationRecord.longitude,
                message = locationRecord.message
            )
        return asrService.addLocation(createLocationRequest)
    }

    fun addImageToLocation(locationId: Int): Completable {
        return asrService.addLocationImage(locationId)
    }

    fun getTeamLocations(teamNumber: Long): Single<List<LocationRecord>> {
        return asrService.getTeamLocations(teamNumber)
            .map { locations -> locations.map { it.toLocationRecord() } }
    }

    fun getUserTeamLocations(): Single<List<LocationRecord>> {
        return asrService.getUserTeamLocations()
            .map { locations -> locations.map { it.toLocationRecord() }}
    }
}