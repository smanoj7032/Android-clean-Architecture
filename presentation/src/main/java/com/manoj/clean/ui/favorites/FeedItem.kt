package com.manoj.clean.ui.favorites

import com.google.gson.annotations.SerializedName

data class FeedItem (
    @SerializedName("link") val link : String,
    @SerializedName("type") val type : String,
    @SerializedName("thumbnail") val thumbnail : String,
    @SerializedName("ratio") val ratio : String
)