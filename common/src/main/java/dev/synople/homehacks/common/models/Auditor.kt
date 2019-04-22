package dev.synople.homehacks.common.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Auditor(
    val id: String = "",
    var name: String = "",
    var scheduledAudits: MutableList<Audit> = mutableListOf() // List of Audit IDs
) : Parcelable