package com.reviling.filamentandroid.data.response

import com.google.gson.annotations.SerializedName

data class GetSearchGamelanInstrumentResponse(

	@field:SerializedName("gamelan_data")
	val gamelanData: List<GamelanDataItem>,

	@field:SerializedName("instrument_data")
	val instrumentData: List<InstrumentDataItem>
)