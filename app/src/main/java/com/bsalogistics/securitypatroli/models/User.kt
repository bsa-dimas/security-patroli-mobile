package com.bsalogistics.securitypatroli.models

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("id") val id: String = "",
    @SerializedName("userid") val userid: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("email_verified_at") val email_verified_at: String = "",
    @SerializedName("disabled") val disabled: String = "",
    @SerializedName("token") val token: String = "",
    @SerializedName("typeuser") val typeuser: String = ""
)
