package org.videotrade.shopot.api

import com.russhwolf.settings.Settings


    fun addValueInStorage(key: String, value: String) {
         val settings = Settings()

        settings.putString(key, value)
    }

    fun getValueInStorage(key: String): String? {
         val settings = Settings()


        val value = settings.getString(key, defaultValue = "error")

        if (value === "error")
            return null


        return value
    }




fun delValueInStorage(key: String) {
    val settings = Settings()

    settings.remove(key)
}

