package com.weikappinc.weikapp.utils

import android.content.Context
import android.content.pm.PackageManager

fun checkIfPermissionIsGranted(permission: String, context: Context) : Boolean {
    return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}