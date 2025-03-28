package me.erasmusteam.odsmaceerasmusapp.data

class ActivityDetailsStruct(
    _image_uris: MutableList<ByteArray>?,
    _activity_id: String,
    _country: String = "",
    _latitude: Double = 0.0,
    _longitude: Double = 0.0,
    _ods: String = "",
    _type: String = "",
    _explanation: String = ""
) {

    var activity_id: String = _activity_id
    var country: String = _country
    var latitude: Double = _latitude
    var longitude: Double = _longitude
    var ods: String = _ods
    var type: String = _type
    var explanation: String = _explanation
    var image_uris: MutableList<ByteArray>? = _image_uris

    override fun toString(): String {
        return "${country}!.!${latitude}!.!${longitude}!.!${ods}!.!${type}!.!${explanation}!.!${activity_id}"
    }

}