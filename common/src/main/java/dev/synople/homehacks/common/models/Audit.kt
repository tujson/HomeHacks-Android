package dev.synople.homehacks.common.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Audit(
    var id: String = "",
    var auditorId: String = "",
    var auditorName: String = "",
    var homeownerId: String = "",
    var homeownerName: String = "",
    var address: String = "", // Audit location
    var startTime: Long = 0,
    var endTime: Long = 0,
    var questions: MutableList<Question> = mutableListOf(),
    var scheduledTime: Long = 0,
    var performedTime: Long = 0
) : Parcelable