package com.sgether.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

// 권한과 관련된 함수를 제공하는 객체
object PermissionHelper {
    // 입력된 권한 목록에서 승인되지 않은 권한 목록들을 반환하는 함수
    fun getDeniedPermissions(context: Context, permissions: List<String>): Array<String> {
        val temp = mutableListOf<String>()
        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                temp.add(permission) // 승인되지 않은 권한을 리스트에 추가
            }
        }
        return temp.toTypedArray() // 리스트를 배열로 변환하여 반환
    }
}