package pl.temomuko.autostoprace.domain.repository

import pl.temomuko.autostoprace.data.remote.AsrService
import pl.temomuko.autostoprace.data.remote.model.CreateLocationRequest
import pl.temomuko.autostoprace.data.remote.model.LocationEntity
import pl.temomuko.autostoprace.domain.MultipartCreator
import pl.temomuko.autostoprace.domain.model.LocationRecord
import rx.Single
import javax.inject.Inject

class LocationsRepository @Inject constructor(
    private val asrService: AsrService,
    private val multipartCreator: MultipartCreator
) {

    fun postLocation(locationRecord: LocationRecord): Single<LocationEntity> {
        val createLocationRequest = CreateLocationRequest(
            latitude = locationRecord.latitude,
            longitude = locationRecord.longitude,
            message = locationRecord.message?.ifBlank { null }
        )
        val imageMultipart = locationRecord.imageUri?.let {
            multipartCreator.createImageMultipartFromUri(it)
        }
        return asrService.addLocation(
            locationRequest = createLocationRequest,
            image = imageMultipart
        )
    }

    fun getTeamLocations(teamNumber: Long): Single<List<LocationRecord>> {
        return asrService.getTeamLocations(teamNumber)
            .map { locations -> locations.map { it.toLocationRecord() } }
    }

    fun getUserTeamLocations(): Single<List<LocationRecord>> {
        return asrService.getUserTeamLocations()
            .map { locations -> locations.map { it.toLocationRecord() } }
    }
}
