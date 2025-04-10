package org.videotrade.shopot.api

import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock

private val settings = Settings()

    fun addValueInStorage(key: String, value: String) {
        settings.putString(key, value)
    }

    fun getValueInStorage(key: String): String? {
        val value = settings.getString(key, defaultValue = "error")

        if (value === "error")
            return null


        return value
    }

fun delValueInStorage(key: String) {
    settings.remove(key)
}

fun incrementAppLaunchCounter() {
    val current = settings.getInt("app_launch_count", 0)
    settings.putInt("app_launch_count", current + 1)
}

fun getAppLaunchCount(): Int {
    return settings.getInt("app_launch_count", 0)
}

fun setSurveyHidden(key: String) {
    settings.putBoolean("survey_hidden_$key", true)
}

fun isSurveyHidden(key: String): Boolean {
    return settings.getBoolean("survey_hidden_$key", false)
}

fun setFirstLaunchDateIfNotSet() {
    if (getValueInStorage("first_launch_date") == null) {
        val now = Clock.System.now().toEpochMilliseconds()
        addValueInStorage("first_launch_date", now.toString())
    }
}

fun getFirstLaunchDate(): Long? {
    return getValueInStorage("first_launch_date")?.toLongOrNull()
}

fun saveLastRateShown(timeMillis: Long) {
    addValueInStorage("last_rate_shown", timeMillis.toString())
}

fun getLastRateShown(): Long? {
    return getValueInStorage("last_rate_shown")?.toLongOrNull()
}

fun saveLastSurveyShown(timeMillis: Long) {
    addValueInStorage("last_survey_shown", timeMillis.toString())
}

fun getLastSurveyShown(): Long? {
    return getValueInStorage("last_survey_shown")?.toLongOrNull()
}

fun markAppRated() {
    addValueInStorage("app_rated", "true")
}

fun isAppRated(): Boolean {
    return getValueInStorage("app_rated") == "true"
}

fun saveRatePostpone() {
    addValueInStorage("rate_postpone", Clock.System.now().toEpochMilliseconds().toString())
}

fun getRatePostpone(): Long? {
    return getValueInStorage("rate_postpone")?.toLongOrNull()
}

fun saveSurveyPostpone() {
    addValueInStorage("survey_postpone", Clock.System.now().toEpochMilliseconds().toString())
}

fun getSurveyPostpone(): Long? {
    return getValueInStorage("survey_postpone")?.toLongOrNull()
}
