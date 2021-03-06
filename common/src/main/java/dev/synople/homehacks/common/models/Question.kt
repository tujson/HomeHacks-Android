package dev.synople.homehacks.common.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Question(
    var id: Int = 0,
    var question: String = "",
    var location: String = "" // Area of the home (e.g. Kitchen, Bathroom 1, etc.)
) : Parcelable