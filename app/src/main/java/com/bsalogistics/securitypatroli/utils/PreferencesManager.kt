package com.bsalogistics.securitypatroli.utils

import android.content.Context
import android.content.SharedPreferences
import com.bsalogistics.securitypatroli.BuildConfig
import com.bsalogistics.securitypatroli.models.User

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(BuildConfig.APPLICATION_ID+"Pref", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun saveDataUser(user: User) {
        val editor = sharedPreferences.edit()
        editor.putString("_id", user.id)
        editor.putString("_userid", user.userid)
        editor.putString("_name", user.name)
        editor.putString("_email", user.email)
        editor.putString("_email_verified_at", user.email_verified_at)
        editor.putString("_disabled", user.disabled)
        editor.putString("_token", user.token)
        editor.putString("_typeuser", user.typeuser)
        editor.apply()
    }

    fun getDataUser(): User {
        return User(
            sharedPreferences.getString("_id", "") ?: "",
            sharedPreferences.getString("_userid", "") ?: "",
            sharedPreferences.getString("_name", "") ?: "",
            sharedPreferences.getString("_email", "") ?: "",
            sharedPreferences.getString("_email_verified_at", "") ?: "",
            sharedPreferences.getString("_disabled", "") ?: "",
            sharedPreferences.getString("_token", "") ?: "",
            sharedPreferences.getString("_typeuser", "") ?: "",
        )
    }

    fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

}