package com.example.breakingnews.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Source(
    @SerializedName("id")
    var id: String,
    @SerializedName("name")
    var name: String
):Parcelable