package dev.synople.homehacks.common.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Homeowner(
    var id: String = "",
    var name: String = "",
    var address: String = ""
) : Parcelable