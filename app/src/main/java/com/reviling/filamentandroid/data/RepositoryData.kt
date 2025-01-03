package com.reviling.filamentandroid.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.protobuf.Api
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.data.database.Pengguna
import com.reviling.filamentandroid.data.preferences.UserModel
import com.reviling.filamentandroid.data.preferences.UserPreferences
import com.reviling.filamentandroid.data.response.AudioGamelanItem
import com.reviling.filamentandroid.data.response.CreateDataSanggarResponse
import com.reviling.filamentandroid.data.response.CreateInstrumentResponse
import com.reviling.filamentandroid.data.response.GamelanDataItem
import com.reviling.filamentandroid.data.response.GetGamelanBaliResponse
import com.reviling.filamentandroid.data.retrofit.ApiConfig
import com.reviling.filamentandroid.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import retrofit2.HttpException
import org.json.JSONObject
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File

class RepositoryData private constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
){

    fun createAudioInstrumentData(
        instrumentId: String,
        namaAudio: String,
        filesAudio: File
    ) = liveData {
        emit(Result.Loading)

        val requestBodyfilesAudio = filesAudio.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val multipartBodyfilesAudio = MultipartBody.Part.createFormData("files", filesAudio.name, requestBodyfilesAudio)
        val instrumentIdRequestBody = instrumentId.toRequestBody("text/plain".toMediaType())
        val namaAudioRequestBody = namaAudio.toRequestBody("text/plain".toMediaType())

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token)
                .createAudioInstrument(
                    instrumentIdRequestBody,
                    namaAudioRequestBody,
                    multipartBodyfilesAudio
                )

            emit(Result.Success(successResponse.message))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun createAudioGamelanData(
        gamelanId: String,
        deskripsiAudio: String,
        namaAudioGamelan: String,
        files: File
    ) = liveData {
        emit(Result.Loading)

        val requestBodyfilesAudio = files.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val multipartBodyfilesAudio = MultipartBody.Part.createFormData("files", files.name, requestBodyfilesAudio)
        val gamelanIdRequestBody = gamelanId.toRequestBody("text/plain".toMediaType())
        val deskripsiAudioRequestBody = deskripsiAudio.toRequestBody("text/plain".toMediaType())
        val namaAudioGamelanRequestBody = namaAudioGamelan.toRequestBody("text/plain".toMediaType())

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token)
                .createAudioGamelan(
                    gamelanIdRequestBody,
                    deskripsiAudioRequestBody,
                    namaAudioGamelanRequestBody,
                    multipartBodyfilesAudio
                )

            emit(Result.Success(successResponse.message))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun updateAudioGamelanData(
        idAudio: String,
        deskripsiAudio: String?,
        namaAudioGamelan: String?,
        files: File?
    ) = liveData {
        emit(Result.Loading)

        val requestBodyfilesAudio = files?.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val multipartBodyfilesAudio =
            requestBodyfilesAudio?.let {
                MultipartBody.Part.createFormData("files", files.name,
                    it
                )
            }
        val deskripsiAudioRequestBody = deskripsiAudio?.toRequestBody("text/plain".toMediaType())
        val namaAudioGamelanRequestBody = namaAudioGamelan?.toRequestBody("text/plain".toMediaType())

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token)
                .updateAudioGamelan(
                    idAudio,
                    deskripsiAudioRequestBody,
                    namaAudioGamelanRequestBody,
                    multipartBodyfilesAudio
                )

            emit(Result.Success(successResponse.message))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun updateDataGamelan(
        idGamelan: String,
        namaGamelan: String?,
        golongan: String?,
        description: String?,
        upacara: List<String>?,
        instrumentId: List<String>?
    ) = liveData {
        emit(Result.Loading)

        val namaGamelanRequestBody = namaGamelan?.toRequestBody("text/plain".toMediaType())
        val golonganGamelanRequestBody = golongan?.toRequestBody("text/plain".toMediaType())
        val descriptionGamelanRequestBody = description?.toRequestBody("text/plain".toMediaType())

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token)
                .updateGamelanData(
                    idGamelan,
                    namaGamelanRequestBody,
                    golonganGamelanRequestBody,
                    descriptionGamelanRequestBody,
                    upacara,
                    instrumentId
                )

            emit(Result.Success(successResponse.message))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun createDataGamelan(
        namaGamelan: String,
        golongan: String,
        description: String,
        upacara: List<String>,
        instrumentId: List<String>
    ) = liveData {
        emit(Result.Loading)

        val namaGamelanRequestBody = namaGamelan.toRequestBody("text/plain".toMediaType())
        val golonganGamelanRequestBody = golongan.toRequestBody("text/plain".toMediaType())
        val descriptionGamelanRequestBody = description.toRequestBody("text/plain".toMediaType())

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token)
                .createGamelanData(
                    namaGamelanRequestBody,
                    golonganGamelanRequestBody,
                    descriptionGamelanRequestBody,
                    upacara,
                    instrumentId
                )

            emit(Result.Success(successResponse.id))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun updateAudioInstrumentData(
        idAudio: String,
        namaAudio: String?,
        filesAudio: File?
    ) = liveData {
        emit(Result.Loading)

        val requestBodyfilesAudio = filesAudio?.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val multipartBodyfilesAudio = requestBodyfilesAudio?.let {
            MultipartBody.Part.createFormData("files", filesAudio.name,
                it
            )
        }
        val namaAudioRequestBody = namaAudio?.toRequestBody("text/plain".toMediaType())

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token)
                .updateAudioInstrumen(
                    idAudio,
                    namaAudioRequestBody,
                    multipartBodyfilesAudio
                )

            emit(Result.Success(successResponse.message))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun createInstrumentData(
        namaInstrumen: String,
        description: String,
        fungsi: String,
        filesImage: List<File>,
        fileTridi: File,
        bahan: List<String>
    ) = liveData {
        emit(Result.Loading)

        val requestBodyImage = filesImage.map { file ->
            val requestBodyFileImage = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("files_image", file.name, requestBodyFileImage)
        }

        val requestBodyTridi = fileTridi.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val multipartBodyTridi = MultipartBody.Part.createFormData("files_tridi", fileTridi.name, requestBodyTridi)
        val namaInstrumenRequestBody = namaInstrumen.toRequestBody("text/plain".toMediaType())
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
        val fungsiRequestBody = fungsi.toRequestBody("text/plain".toMediaType())

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token)
                .createIntrumentData(
                    namaInstrumenRequestBody,
                    descriptionRequestBody,
                    fungsiRequestBody,
                    requestBodyImage,
                    multipartBodyTridi,
                    bahan
                )

            emit(Result.Success(successResponse.response.id))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun createDataSanggar(
        fileImage: File,
        namaSanggar: String,
        noTelepon: String,
        namaJalan: String,
        kodePost: String,
        deskripsi: String,
        idDesa: String,
        gamelanId: MutableList<String>
    ) = liveData {
        emit(Result.Loading)

        val requestBodyImage = fileImage.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val multipartBodyImage = MultipartBody.Part.createFormData("files", fileImage.name, requestBodyImage)
        val namaSanggarRequestBody = namaSanggar.toRequestBody("text/plain".toMediaType())
        val noTeleponRequestBody = noTelepon.toRequestBody("text/plain".toMediaType())
        val namaJalanRequestBody = namaJalan.toRequestBody("text/plain".toMediaType())
        val kodePostRequestBody = kodePost.toRequestBody("text/plain".toMediaType())
        val deskripsiRequestBody = deskripsi.toRequestBody("text/plain".toMediaType())
        val idDesaRequestBody = idDesa.toRequestBody("text/plain".toMediaType())

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token)
                .createDataSanggar(
                    multipartBodyImage,
                    namaSanggarRequestBody,
                    noTeleponRequestBody,
                    namaJalanRequestBody,
                    kodePostRequestBody,
                    deskripsiRequestBody,
                    idDesaRequestBody,
                    gamelanId
                )

            emit(Result.Success(successResponse.response.message))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun updateDataInstrumen(
        id: String,
        flagImage: String?,
        namaInstrumen: String?,
        description: String?,
        fungsi: String?,
        filesImage: List<File>?,
        fileTridi: File?,
        bahan: List<String>?
    ) = liveData {
        emit(Result.Loading)

        val requestBodyImage = filesImage?.map { file ->
            val requestBodyFileImage = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("files_image", file.name, requestBodyFileImage)
        }

        val requestBodyTridi = fileTridi?.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val multipartBodyTridi = requestBodyTridi?.let {
            MultipartBody.Part.createFormData("files_tridi", fileTridi.name,
                it
            )
        }
        val namaInstrumenRequestBody = namaInstrumen?.toRequestBody("text/plain".toMediaType())
        val descriptionRequestBody = description?.toRequestBody("text/plain".toMediaType())
        val fungsiRequestBody = fungsi?.toRequestBody("text/plain".toMediaType())
        val flagImageRequestBody = flagImage?.toRequestBody("text/plain".toMediaType())

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token)
                .updateInstrumentData(
                    id,
                    flagImageRequestBody,
                    namaInstrumenRequestBody,
                    descriptionRequestBody,
                    fungsiRequestBody,
                    requestBodyImage,
                    multipartBodyTridi,
                    bahan
                )

            emit(Result.Success(successResponse.message))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun updateDataSanggar(
        id: String,
        fileImage: File?,
        namaSanggar: String?,
        noTelepon: String?,
        namaJalan: String?,
        kodePost: String?,
        deskripsi: String?,
        idDesa: String?,
        gamelanId: MutableList<String>?
    ) = liveData {
        emit(Result.Loading)

        val requestBodyImage = fileImage?.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val multipartBodyImage = requestBodyImage?.let {
            MultipartBody.Part.createFormData("files",
                fileImage.name, it
            )
        }

        val namaSanggarRequestBody = namaSanggar?.toRequestBody("text/plain".toMediaType())
        val noTeleponRequestBody = noTelepon?.toRequestBody("text/plain".toMediaType())
        val namaJalanRequestBody = namaJalan?.toRequestBody("text/plain".toMediaType())
        val kodePostRequestBody = kodePost?.toRequestBody("text/plain".toMediaType())
        val deskripsiRequestBody = deskripsi?.toRequestBody("text/plain".toMediaType())
        val idDesaRequestBody = idDesa?.toRequestBody("text/plain".toMediaType())

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token)
                .updateDataSanggar(
                    id,
                    multipartBodyImage,
                    namaSanggarRequestBody,
                    noTeleponRequestBody,
                    namaJalanRequestBody,
                    kodePostRequestBody,
                    deskripsiRequestBody,
                    idDesaRequestBody,
                    gamelanId
                )

            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun fetchGolonganGamelan() = liveData {
        emit(Result.Loading)

        try {
            val response = apiService.fetchGolonganList()

            emit(Result.Success(response.golonganList))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun fetchInstrumentOnlyNameId() = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val response = ApiConfig.getApiService(user.access_token).fetchInstrumentOnlyNamesId()

            emit(Result.Success(response.instrumentData))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun deleteInstrumentById(id: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val deletedResponse = ApiConfig.getApiService(user.access_token).deleteInstrumentById(id)

            emit(Result.Success(deletedResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun deleteAudioByIdList(id: MutableList<String>) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val deletedResponse = ApiConfig.getApiService(user.access_token).deleteManyAudio(id)

            emit(Result.Success(deletedResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun getRoleList() = liveData {
        emit(Result.Loading)

        try {
            val successResponse = apiService.getAllRole()
            emit(Result.Success(successResponse.roleList))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun fetchAudioGamelanByIdGamelan(id: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).fetchAudioGamelanByIdGamelan(id)
            emit(Result.Success(successResponse.audioArray))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun fetchAudioInstrumentById(id: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).fetchAudioInstrumentByIdInstrument(id)
            emit(Result.Success(successResponse.audioArray))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun deleteAudioByItsId(idAudioInstrument: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).deleteAudioByItsId(idAudioInstrument)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun deleteAudioGamelanByItsId(idAudioGamelan: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).deleteAudioGamelanByItsId(idAudioGamelan)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun deleteManyAudioByItsGamelanId(idGamelan: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).deleteManyAudioByItsGamelanId(idGamelan)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun deleteGamelanDataByItsId(idGamelan: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).deleteGamelanDataById(idGamelan)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun registerUser(name: String, email: String, password: String, role: String) = liveData {
        emit(Result.Loading)
        val namaRequestBody = name.toRequestBody("text/plain".toMediaType())
        val emailRequestBody = email.toRequestBody("text/plain".toMediaType())
        val passwordRequestBody = password.toRequestBody("text/plain".toMediaType())
        val roleRequestBody = role.toRequestBody("text/plain".toMediaType())

        try {
            val successResponse = apiService.registerUser(namaRequestBody, emailRequestBody, passwordRequestBody, roleRequestBody)
            emit(Result.Success("Thanks for register ${successResponse.nama}"))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun loginUser(email: String, password: String) = liveData {
        emit(Result.Loading)

        try {
            val successResponse = apiService.loginUser(email, password)

            val accessToken = successResponse.accessToken
            val userId = successResponse.userId
            val nama = successResponse.nama
            val email = successResponse.email
            val fotoProfile = successResponse.fotoProfile
            val isLogin = true
            val role = successResponse.role
            val status = successResponse.status

            saveSession(UserModel(nama, accessToken, email, userId, fotoProfile, role, status, isLogin))

            emit(Result.Success("Thanks for login ${successResponse.nama}"))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun getAllGamelanBaliData() = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getAllGamelanBali()

            emit(Result.Success(successResponse.gamelanData))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }

    }

    fun getSearchGamelanInstrument(namaGamelan: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getSearchGamelanInstrument(namaGamelan)
            Log.d("IsiSuccessResponse", successResponse.toString())
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun uploadPhotoUser(idUser: String, files: File) = liveData {
        emit(Result.Loading)

        try {
            val requestBodyImage = files.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val multipartBodyImage = MultipartBody.Part.createFormData("files", files.name, requestBodyImage)

            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).uploadPhotoProfile(idUser, multipartBodyImage)
            Log.d("IsiSuccessResponse", successResponse.message.toString())
            emit(Result.Success(successResponse.message.toString()))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun updateUsername(idUser: String, nama: String) = liveData {
        emit(Result.Loading)

        try {
            val namaRequestBody = nama.toRequestBody("text/plain".toMediaType())

            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).updateUsername(idUser, namaRequestBody)

            emit(Result.Success(successResponse.message))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun deleteSanggarData(idSanggar: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).deleteDataSanggar(idSanggar)
            Log.d("IsiSuccessResponse", successResponse)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun geDetailGamelanInstrument(id: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).geDetailGamelanInstrument(id)
            Log.d("IsiSuccessResponse", successResponse.toString())
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun getListDesa(id: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getListDesa(id)
            Log.d("IsiSuccessResponse", successResponse.toString())
            emit(Result.Success(successResponse.desaData))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun getAllListAlamatByDesaId(id: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getAllAlamatByIdDesa(id)
            Log.d("IsiSuccessResponse", successResponse.toString())
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun getListKecamatan(id: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getListKecamatan(id)
            Log.d("IsiSuccessResponse", successResponse.toString())
            emit(Result.Success(successResponse.kecamatanData))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun getListKabupaten() = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getListKabupaten()
            Log.d("IsiSuccessResponse", successResponse.toString())
            emit(Result.Success(successResponse.kabupatenData))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun geDetailGamelanByInstrumentId(id: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).geDetailGamelanByInstrumentId(id)
            Log.d("IsiSuccessResponse", successResponse.toString())
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error("There is no Gamelan Data using this Instrument"))
        }
    }

    fun getGamelanByIdList(idList: MutableList<String>) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).fetchGamelanByIdList(idList)
            emit(Result.Success(successResponse.gamelanData))

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun geDetailSanggarbyId(id: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).geDetailSanggarbyId(id)
            Log.d("IsiSuccessResponse", successResponse.toString())
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun getSanggarByName(namaSanggar: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getSanggarByName(namaSanggar)
            Log.d("IsiSuccessResponse", successResponse.toString())
            emit(Result.Success(successResponse.sanggarData))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun getSanggarByIdCreator(id: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getSanggarByIdCreator(id)
            Log.d("IsiSuccessResponse", successResponse.toString())
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun geDetailInstrumentById(id: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).geDetailInstrumentById(id)
            Log.d("IsiSuccessResponse", successResponse.toString())
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    fun getAllInstrumentBaliData() = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getAllInstrumentBali()

            emit(Result.Success(successResponse.instrumentData))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }

    }

    fun getInstrumentByName(namaInstrument: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getAllInstrumentByName(namaInstrument)

            emit(Result.Success(successResponse.instrumentData))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }

    }

    fun getGamelanByGolongan(golongan: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getGamelanByGolongan(golongan)

            emit(Result.Success(successResponse.gamelanData))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }

    }

    fun getUserDatabyId(idUser: String) = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getUserById(idUser)

            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }

    }

    fun getAllSanggarBaliData() = liveData {
        emit(Result.Loading)

        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val successResponse = ApiConfig.getApiService(user.access_token).getAllSanggarBali()

            emit(Result.Success(successResponse.sanggarData))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("HttpException", "Error: ${e.message}, Code: ${e.code()}, Body: $errorBody")
            emit(Result.Error("Sorry, ${errorBody ?: "Unknown HTTP error"}"))
        } catch (e: IOException) {
            Log.e("IOException", "Network Error: ${e.message}")
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Log.e("Exception", "Unexpected Error: ${e.message}")
            emit(Result.Error(e.localizedMessage ?: "Unknown error occurred"))
        }

    }

    suspend fun saveSession(userModel: UserModel){
        userPreferences.saveSession(userModel)
    }

    fun getSession(): Flow<UserModel> {
        return userPreferences.getSession()
    }

    suspend fun logout(){
        userPreferences.logout()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: RepositoryData? = null
        fun getInstance(context: Context, apiService: ApiService, userPreferences: UserPreferences) =
            instance ?: synchronized(this) {
                instance ?: RepositoryData(context, apiService, userPreferences)
            }.also { instance = it }
    }
}