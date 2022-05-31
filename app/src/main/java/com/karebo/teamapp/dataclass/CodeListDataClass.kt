package com.karebo.teamapp.dataclass

import com.google.gson.annotations.SerializedName

data class CodeListDataClass(
    @SerializedName("Electricity Meter")
    val ElectricityMeter: List<ElectricityMeter>,
    @SerializedName("Exclusion Code")
    val ExclusionCode: List<String>,
    @SerializedName("Meter Status")
    val MeterStatus: List<String>,
    @SerializedName("SHEQ")
    val SHEQ: List<String>,
    @SerializedName("Site Access")
    val SiteAccess: List<String>,
    @SerializedName("Site Status")
    val SiteStatus: List<String>
)