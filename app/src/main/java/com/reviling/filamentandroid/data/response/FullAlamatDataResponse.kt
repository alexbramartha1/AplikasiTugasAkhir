package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class FullAlamatDataResponse(

	@field:SerializedName("kecamatan_data")
	val kecamatanData: List<KecamatanDataItem>,

	@field:SerializedName("desa_data")
	val desaData: List<DesaDataItem>,

	@field:SerializedName("kabupaten_id")
	val kabupatenId: String
)