package com.shanudevcodes.newsbits.data

fun shortenName(name: String): String {
    val words = name.trim().split("\\s+".toRegex())
    return if (words.size > 2) {
        "${words[0]} ${words[1]}..."
    } else {
        name
    }
}