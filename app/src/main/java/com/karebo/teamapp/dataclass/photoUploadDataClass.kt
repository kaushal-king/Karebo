package com.karebo.teamapp.dataclass

import com.google.gson.annotations.SerializedName

class photoUploadDataClass  (

    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("body")
    var bodyy: String ,
)