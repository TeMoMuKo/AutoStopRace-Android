package pl.temomuko.autostoprace.domain

import android.content.Context
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MultipartCreator @Inject constructor(
    private val context: Context
) {

    fun createImageMultipartFromUri(uri: Uri): MultipartBody.Part {
        val openInputStream = context.contentResolver.openInputStream(uri)
        return MultipartBody.Part.createFormData(
            "image",
            null,
            RequestBody.create(
                MediaType.parse("image/jpeg"), openInputStream?.readBytes() ?: byteArrayOf()
            )
        )
    }
}
