package com.bsalogistics.securitypatroli.network

import com.google.gson.annotations.SerializedName


data class LoginBody(
    @SerializedName("email")
    var email: String,
    @SerializedName("password")
    var password: String,
)

data class PhotoArea (
    @SerializedName("id") var id: String = "",
    @SerializedName("filename") var filename: String = "",
    @SerializedName("url_photo") var url_photo: String = ""
)

data class AreaDetail (

    @SerializedName("id") var id: String = "",
    @SerializedName("userid") var userid: String = "",
    @SerializedName("area") var area: String = "",
    @SerializedName("latitude") var latitude: Double = 0.0,
    @SerializedName("longitude") var longitude: Double = 0.0,
    @SerializedName("latitude_actual") var latitude_actual: Double = 0.0,
    @SerializedName("longitude_actual") var longitude_actual: Double = 0.0,
    @SerializedName("keterangan") var keterangan: String? = "",
    @SerializedName("created_at") var created_at: String? = "",
    @SerializedName("updated_at") var updated_at: String? = "",
    @SerializedName("photos") var photos: MutableList<PhotoArea>? = mutableListOf(),

)

data class AreaFormTransaction (

    @SerializedName("id") var id: String = "",
    @SerializedName("userid") var userid: String = "",
    @SerializedName("area") var area: String = "",
    @SerializedName("latitude") var latitude: Double = 0.0,
    @SerializedName("longitude") var longitude: Double = 0.0,
    @SerializedName("latitude_actual") var latitude_actual: Double = 0.0,
    @SerializedName("longitude_actual") var longitude_actual: Double = 0.0,
    @SerializedName("keterangan") var keterangan: String = "",
    @SerializedName("created_at") var created_at: String? = "",
    @SerializedName("updated_at") var updated_at: String? = ""

)

data class Area (
    @SerializedName("id") var id: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("latitude") var latitude: Double = 0.0,
    @SerializedName("longitude") var longitude: Double = 0.0,
)

data class FormAddAreaBody(
    @SerializedName("userid") var userid: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("latitude") var latitude: Double = 0.0,
    @SerializedName("longitude") var longitude: Double = 0.0,
)

data class FormAreaBody(
    @SerializedName("userid") var userid: String,
    @SerializedName("area") var area: String,
    @SerializedName("latitude_actual") var latitude_actual: Double,
    @SerializedName("longitude_actual") var longitude_actual: Double,
    @SerializedName("distance") var distance: Double,
    @SerializedName("keterangan") var keterangan: String = "",
)
