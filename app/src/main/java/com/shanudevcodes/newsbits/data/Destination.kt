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
    object HOMESCREEN : HomeDestination()
    @Serializable
    data class NEWSDETAILSCREEN(val newsId : Int, val news: String) : HomeDestination()
    @Serializable
    object SEARCHRESULTDETAILSCREEN : HomeDestination()
}

@Serializable
sealed class BookmarkDestination{
    @Serializable
    object BOOKMARKSCREEN : BookmarkDestination()
    @Serializable
    data class BOOKMARKDETAILSCREEN(val newsId: String) : BookmarkDestination()
}

@Serializable
sealed class SearchDestination{
    @Serializable
    object HOMESEARCHSCREEN : SearchDestination()
    @Serializable
    object SEARCHRESULTSCREEN : SearchDestination()
}