package me.erasmusteam.odsmaceerasmusapp.data

data class ActivityJsonData(
    val activity_state: Int,
    val country: String,
    val explanation: String,
    val latitude: Double,
    val longitude: Double,
    val ods: String,
    val type: String,
    val id: String,
    val user_id: String,
)
