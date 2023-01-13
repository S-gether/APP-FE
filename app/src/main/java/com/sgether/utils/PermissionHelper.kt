package com.sgether.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionHelper {
    fun getDeniedPermissions(context: Context, permissions: List<String>): Array<String> {
        val temp = mutableListOf<String>()
        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                temp.add(permission)
            }
        }
        return temp.toTypedArray()
    }
}