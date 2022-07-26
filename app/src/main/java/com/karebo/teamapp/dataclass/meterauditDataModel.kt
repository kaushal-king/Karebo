package com.karebo.teamapp.dataclass

import com.google.gson.annotations.SerializedName

class meterauditDataModel(

    @SerializedName("vertices")
    var vertices : List<String>? = null,
    @SerializedName("sgCode")
    var sgCode: String? = null,
    @SerializedName("jobCardId")
    var jobCardId: String? = null,
    @SerializedName("longitude")
    var latitude: Double? = null,
    @SerializedName("latitude")
    var longitude: Double? = null,
    @SerializedName("project")
    var project: String? = null,
    @SerializedName("team")
    var team: String? = null,
    @SerializedName("cardType")
    var cardType: String? = null,
    @SerializedName("municipality")
    var municipality: String? = null,
    @SerializedName("parcelAddress")
    var address: String? = null
    )
