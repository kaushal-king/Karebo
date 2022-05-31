package com.karebo.teamapp.dataclass

data class MeterDataModel(
    val AccountNumber: String,
    val CustomerName: String,
    val ETag: Any,
    val KRN: Int,
    val MeterType: String,
    val Model: String,
    val Municipality: String,
    val PartitionKey: Any,
    val RowKey: Any,
    val Serial: String,
    val Timestamp: String
)