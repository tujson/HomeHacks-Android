package dev.synople.homehacks.common.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Question(
    var area: String = "", // Area of the home (e.g. Kitchen, Bathroom 1, etc.)
    var question: String = "",
    var answer: String = "",
    var comments: String = "",
    var images: MutableList<String> = mutableListOf()// IDs of the images
) : Parcelable