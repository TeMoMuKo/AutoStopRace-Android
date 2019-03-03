package pl.temomuko.autostoprace.domain.repository

import android.net.Uri
import pl.temomuko.autostoprace.data.remote.AsrService
import pl.temomuko.autostoprace.data.remote.model.CreateLocationRequest
import pl.temomuko.autostoprace.data.remote.model.LocationEntity
import pl.temomuko.autostoprace.domain.MultipartCreator
import pl.temomuko.autostoprace.domain.model.LocationRecord
import rx.Completable
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
        return asrService.addLocation(createLocationRequest)
            .flatMap {
                val imageUri = locationRecord.imageUri
                if (imageUri != null) {
                    //todo handle transaction
                    addImageToLocation(locationId = it.id, imageUri = imageUri)
                        .andThen(Single.just(it))
                } else {
                    Single.just(it)
                }
            }
    }

    private fun addImageToLocation(locationId: Long, imageUri: Uri): Completable {
        val imageMultipart = multipartCreator.createImageMultipartFromUri(imageUri)
        return asrService.addLocationImage(locationId, imageMultipart)
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
