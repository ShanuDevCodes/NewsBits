package com.shanudevcodes.newsbits.data

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {
    @Serializable
    object HOMESCREEN : Destination()
    @Serializable
    data class NEWSDETAILSCREEN(val newsId : Int, val news: String) : Destination()
}