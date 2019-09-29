package dev.synople.homehacks.common.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Response(
    var id: Int = 0,
    var response: String = "",
    var comments: String = "",
    var images: MutableList<String> = mutableListOf()
) : Parcelable