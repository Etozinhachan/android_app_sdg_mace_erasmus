package me.erasmusteam.odsmaceerasmusapp.data

data class ActivityData(
    val activity_state: Int,
    val country: String,
    val explanation: String,
    val latitude: Double,
    val longitude: Double,
    val ods: String,
    val type: String,
    val id: String,
    val user_id: String,
    val image_types: MutableList<String>,
    val images: MutableList<ByteArray>? = mutableListOf()
)
