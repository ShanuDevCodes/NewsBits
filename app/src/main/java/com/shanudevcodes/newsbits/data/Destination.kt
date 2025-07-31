package com.shanudevcodes.newsbits.data

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {
    @Serializable
    object HOME : Destination()
    @Serializable
    object EXPLORE : Destination()
    @Serializable
    object BITDIGEST : Destination()
    @Serializable
    object BOOKMARKS : Destination()
    @Serializable
    object PROFILE : Destination()
}

@Serializable
sealed class HomeDestination{
    @Serializable
    object HOMESCREEN : HomeDestination()
    @Serializable
    data class SEARCHRESULTDETAILSCREEN(val link: String) : HomeDestination()
    @Serializable
    data class BOOKMARKDETAILSCREEN(val newsId: String) : HomeDestination()
    @Serializable
    object SETTINGS : HomeDestination()
    @Serializable
    object HELPCENTER : HomeDestination()
    @Serializable
    object ABOUT : HomeDestination()

}

@Serializable
sealed class SearchDestination{
    @Serializable
    object HOMESEARCHSCREEN : SearchDestination()
    @Serializable
    data class SEARCHRESULTSCREEN(val query: String) : SearchDestination()
}