package dev.synople.homehacks.common.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Response(
    var id: Int = 0,
    var response: String = "",
    var comments: String = "",
    var images: MutableList<String> = mutableListOf(),
    @get:Exclude var imageUris: MutableList<Uri> = mutableListOf()
) : Parcelable