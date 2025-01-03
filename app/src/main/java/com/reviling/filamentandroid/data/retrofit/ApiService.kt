package com.reviling.filamentandroid.data.retrofit

import com.reviling.filamentandroid.data.database.Pengguna
import com.reviling.filamentandroid.data.response.CreateAudioInstrumentResponse
import com.reviling.filamentandroid.data.response.CreateDataSanggarResponse
import com.reviling.filamentandroid.data.response.CreateGamelanResponse
import com.reviling.filamentandroid.data.response.CreateInstrumentResponse
import com.reviling.filamentandroid.data.response.DeleteGamelanResponse
import com.reviling.filamentandroid.data.response.DesaListResponse
import com.reviling.filamentandroid.data.response.FetchGamelanAudioResponse
import com.reviling.filamentandroid.data.response.FetchGolonganListResponse
import com.reviling.filamentandroid.data.response.FetchInstrumentAudioResponse
import com.reviling.filamentandroid.data.response.FetchRoleResponse
import com.reviling.filamentandroid.data.response.FullAlamatDataResponse
import com.reviling.filamentandroid.data.response.GetGamelanBaliResponse
import com.reviling.filamentandroid.data.response.GetInstrumentBaliResponse
import com.reviling.filamentandroid.data.response.GetSanggarBaliResponse
import com.reviling.filamentandroid.data.response.GetSearchGamelanInstrumentResponse
import com.reviling.filamentandroid.data.response.GetUserDataResponse
import com.reviling.filamentandroid.data.response.KabupatenListResponse
import com.reviling.filamentandroid.data.response.KecamatanListResponse
import com.reviling.filamentandroid.data.response.LoginUserResponse
import com.reviling.filamentandroid.data.response.UpdateDataAudioInstrumentResponse
import com.reviling.filamentandroid.data.response.UpdateDataInstrumenResponse
import com.reviling.filamentandroid.data.response.UpdateDataSanggarResponse
import com.reviling.filamentandroid.data.response.UpdateGamelanBaliResponse
import com.reviling.filamentandroid.data.response.UpdateUsernameResponse
import com.reviling.filamentandroid.data.response.UploadPhotoUserResponse
import com.reviling.filamentandroid.data.response.UserGetByIdResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Multipart
    @POST("api/userdata/registeruser")
    suspend fun registerUser(
        @Part("nama") nama: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("role_input") role: RequestBody
    ): GetUserDataResponse

    @FormUrlEncoded
    @POST("token")
    suspend fun loginUser(
        @Field("username") email: String,
        @Field("password") password: String
    ): LoginUserResponse

    @GET("api/gamelandata/instrumentid/{nama_gamelan}")
    suspend fun getSearchGamelanInstrument(
        @Path("nama_gamelan") namaGamelan: String
    ): GetSearchGamelanInstrumentResponse

    @GET("api/instrumendata/getspecificbyname/{nama_instrument}")
    suspend fun getAllInstrumentByName(
        @Path("nama_instrument") namaInstrument: String
    ): GetInstrumentBaliResponse

    @GET("api/gamelanbali/fetch/specific/{id}")
    suspend fun geDetailGamelanInstrument(
        @Path("id") id: String
    ): GetSearchGamelanInstrumentResponse

    @GET("api/gamelandata/gamelanbyinstrumentid/{id}")
    suspend fun geDetailGamelanByInstrumentId(
        @Path("id") id: String
    ): GetGamelanBaliResponse

    @GET("api/sanggardata/getbyidcreator/{id}")
    suspend fun getSanggarByIdCreator(
        @Path("id") id: String
    ): GetSanggarBaliResponse

    @GET("api/sanggardata/getbyid/{id}")
    suspend fun geDetailSanggarbyId(
        @Path("id") id: String
    ): GetSanggarBaliResponse

    @GET("api/sanggardata/getbyname/{name}")
    suspend fun getSanggarByName(
        @Path("name") name: String
    ): GetSanggarBaliResponse

    @GET("api/instrumendata/getone/{id}")
    suspend fun geDetailInstrumentById(
        @Path("id") id: String
    ): GetInstrumentBaliResponse

    @GET("api/gamelandata/gamelanbygolongan/{golongan}")
    suspend fun getGamelanByGolongan(
        @Path("golongan") golongan: String
    ): GetGamelanBaliResponse

    @GET("api/getallrole/listrole")
    suspend fun getAllRole(): FetchRoleResponse

    @Multipart
    @POST("api/sanggardata/create")
    suspend fun createDataSanggar(
        @Part files: MultipartBody.Part,
        @Part("nama_sanggar") namaSanggar: RequestBody,
        @Part("no_telepon") noTelepon: RequestBody,
        @Part("nama_jalan") namaJalan: RequestBody,
        @Part("kode_pos") kodePost: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("id_desa") idDesa: RequestBody,
        @Part("gamelan_id") gamelanlist: MutableList<String>
    ): CreateDataSanggarResponse

    @Multipart
    @PUT("api/sanggardata/update/{id}")
    suspend fun updateDataSanggar(
        @Path("id") id: String,
        @Part files: MultipartBody.Part?,
        @Part("nama_sanggar") namaSanggar: RequestBody?,
        @Part("no_telepon") noTelepon: RequestBody?,
        @Part("nama_jalan") namaJalan: RequestBody?,
        @Part("kode_pos") kodePost: RequestBody?,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part("id_desa") idDesa: RequestBody?,
        @Part("gamelan_id") gamelanlist: MutableList<String>?
    ): UpdateDataSanggarResponse

    @GET("api/gamelanbali/fetchall")
    suspend fun getAllGamelanBali(): GetGamelanBaliResponse

    @GET("api/userdata/getuserbyid/{id}")
    suspend fun getUserById(
        @Path("id") idUser: String
    ): UserGetByIdResponse

    @GET("api/instrumendata/getall")
    suspend fun getAllInstrumentBali(): GetInstrumentBaliResponse

    @GET("api/sanggardata/get")
    suspend fun getAllSanggarBali(): GetSanggarBaliResponse

    @DELETE("api/sanggardata/delete/{id}")
    suspend fun deleteDataSanggar(
        @Path("id") idSanggar: String
    ): String

    @Multipart
    @POST("api/files/uploadphotoprofile/{id}")
    suspend fun uploadPhotoProfile(
        @Path("id") id: String,
        @Part files: MultipartBody.Part
    ): UploadPhotoUserResponse

    @Multipart
    @PUT("api/userdata/updateprofile/{id}")
    suspend fun updateUsername(
        @Path("id") id: String,
        @Part("nama") nama: RequestBody
    ): UpdateUsernameResponse

    @GET("api/getkabupaten/all")
    suspend fun getListKabupaten(): KabupatenListResponse

    @GET("api/getkecamatan/bykabupatenid/{id}")
    suspend fun getListKecamatan(
        @Path("id") id: String
    ): KecamatanListResponse

    @GET("api/getdesa/bykecamatanid/{id}")
    suspend fun getListDesa(
        @Path("id") id: String
    ): DesaListResponse

    @GET("api/getallalamat/bydesaid/{id}")
    suspend fun getAllAlamatByIdDesa(
        @Path("id") id: String
    ): FullAlamatDataResponse

    @FormUrlEncoded
    @POST("api/gamelandata/gamelanlistbyid")
    suspend fun fetchGamelanByIdList(
        @Field("id") idLIst: MutableList<String>
    ): GetGamelanBaliResponse

    @Multipart
    @POST("api/instrumendata/create")
    suspend fun createIntrumentData(
        @Part("nama") namaInstrumen: RequestBody,
        @Part("desc") description: RequestBody,
        @Part("fungsi") fungsi: RequestBody,
        @Part filesImage: List<MultipartBody.Part>,
        @Part fileTridi: MultipartBody.Part,
        @Part("bahan") bahan: List<String>
    ): CreateInstrumentResponse

    @Multipart
    @PUT("api/instrumendata/update/{id}")
    suspend fun updateInstrumentData(
        @Path("id") idInstrument: String,
        @Part("flagImage") flagImage: RequestBody? = null,
        @Part("nama") namaInstrumen: RequestBody? = null,
        @Part("desc") description: RequestBody? = null,
        @Part("fungsi") fungsi: RequestBody? = null,
        @Part filesImage: List<MultipartBody.Part>? = null,
        @Part fileTridi: MultipartBody.Part? = null,
        @Part("bahan") bahan: List<String>? = null
    ): UpdateDataInstrumenResponse

    @GET("api/audiogamelanbali/fetch/bygamelanid/{id}")
    suspend fun fetchAudioGamelanByIdGamelan(
        @Path("id") id: String
    ): FetchGamelanAudioResponse

    @Multipart
    @POST("api/gamelandata/uploadaudio")
    suspend fun createAudioGamelan(
        @Part("id_gamelan") gamelanId: RequestBody,
        @Part("deskripsi") deskripsiAudio: RequestBody,
        @Part("nama_audio") namaAudioGamelan: RequestBody,
        @Part files: MultipartBody.Part
    ): CreateAudioInstrumentResponse

    @GET("api/audioinstrumen/fetch/byinstrumenid/{id}")
    suspend fun fetchAudioInstrumentByIdInstrument(
        @Path("id") id: String
    ): FetchInstrumentAudioResponse

    @DELETE("api/audioinstrumen/deletedataaudiobyid/{id}")
    suspend fun deleteAudioByItsId(
        @Path("id") idAudioInstrument: String
    ): String

    @DELETE("api/audiogamelanbali/deletedataspesifik/{id}")
    suspend fun deleteAudioGamelanByItsId(
        @Path("id") idAudioGamelan: String
    ): String

    @Multipart
    @POST("api/audioinstrumen/uploadaudio")
    suspend fun createAudioInstrument(
        @Part("instrument_id") instrumentId: RequestBody,
        @Part("nama_audio") namaAudio: RequestBody,
        @Part files: MultipartBody.Part
    ): CreateAudioInstrumentResponse

    @DELETE("api/instrumendata/delete/{id}")
    suspend fun deleteInstrumentById(
        @Path("id") idInstrument: String
    ): String

    @FormUrlEncoded
    @POST("api/audioinstrumen/deleteaudioinstrument/manyid")
    suspend fun deleteManyAudio(
        @Field("id") idListAudio: MutableList<String>
    ): String

    @DELETE("api/audiogamelanbali/deletedata/{id}")
    suspend fun deleteManyAudioByItsGamelanId(
        @Path("id") idGamelan: String
    ): String

    @DELETE("api/gamelandata/deletedata/{id}")
    suspend fun deleteGamelanDataById(
        @Path("id") idGamelan: String
    ): DeleteGamelanResponse

    @Multipart
    @PUT("api/audioinstrumen/updateaudio/{id}")
    suspend fun updateAudioInstrumen(
        @Path("id") idAudio: String,
        @Part("nama_audio") namaAudio: RequestBody? = null,
        @Part filesAudio: MultipartBody.Part? = null
    ): UpdateDataAudioInstrumentResponse

    @Multipart
    @PUT("api/audiogamelanbali/updateaudio/{id}")
    suspend fun updateAudioGamelan(
        @Path("id") idAudio: String,
        @Part("deskripsi") deskripsiAudio: RequestBody? = null,
        @Part("nama_audio") namaAudioGamelan: RequestBody? = null,
        @Part files: MultipartBody.Part? = null
    ): UpdateDataAudioInstrumentResponse

    @Multipart
    @POST("api/gamelanbali/createdata")
    suspend fun createGamelanData(
        @Part("nama_gamelan") namaGamelan: RequestBody,
        @Part("golongan") golongan: RequestBody,
        @Part("description") description: RequestBody,
        @Part("upacara") upacara: List<String>,
        @Part("instrument_id") instrumentId: List<String>
    ): CreateGamelanResponse

    @Multipart
    @PUT("api/gamelandata/updatedata/{id}")
    suspend fun updateGamelanData(
        @Path("id") idGamelan: String,
        @Part("nama_gamelan") namaGamelan: RequestBody? = null,
        @Part("golongan") golongan: RequestBody? = null,
        @Part("description") description: RequestBody? = null,
        @Part("upacara") upacara: List<String>? = null,
        @Part("instrument_id") instrumentId: List<String>? = null
    ): UpdateGamelanBaliResponse

    @GET("api/getallgolongan/listgolongan")
    suspend fun fetchGolonganList(): FetchGolonganListResponse

    @GET("api/fetchinstrument/onlynameandid")
    suspend fun fetchInstrumentOnlyNamesId(): GetInstrumentBaliResponse

}