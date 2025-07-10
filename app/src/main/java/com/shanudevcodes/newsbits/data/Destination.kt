package com.shanudevcodes.newsbits.data

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {
    @Serializable
    object HOME : Destination()
    @Serializable
    object BOOKMARKS : Destination()
}

@Serializable
sealed class HomeDestination{
    @Serializable
    object HOMESCREEN : Destination()
    @Serializable
    data class NEWSDETAILSCREEN(val newsId : Int, val news: String) : Destination()
}

@Serializable
sealed class BookmarkDestination{
    @Serializable
    object BOOKMARKSCREEN : Destination()
    @Serializable
    data class BOOKMARKDETAILSCREEN(val newsId: Int) : Destination()
}