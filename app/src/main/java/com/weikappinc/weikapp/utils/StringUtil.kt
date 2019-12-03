package com.weikappinc.weikapp.utils

fun getFileName(path: String): String? {
    val result: String?

    val cut = path.lastIndexOf('/')
    val upon = path.lastIndexOf('.')

    if (cut != -1) {
        result = cut.plus(1).let { path.substring(it, upon) }
    } else {
        result = null
    }

    return result
}