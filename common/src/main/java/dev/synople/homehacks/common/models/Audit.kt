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
    var address: String = "",
    var questions: MutableList<Question> = mutableListOf(),
    var availabilities: MutableList<Long> = mutableListOf(), // Epoch time. Value indicates possible audit start time. Audits are of length auditTimeLength long.
    var scheduledTime: Long = 0, // When this audit is scheduled for
    var performedTime: Long = 0 // When this audit is performed
) : Parcelable