package com.karebo.teamapp.dataclass

import com.google.gson.annotations.SerializedName

class subMeterModel
    (
    @SerializedName("id")
    var id: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("imgId")
    var imgId: String,
)