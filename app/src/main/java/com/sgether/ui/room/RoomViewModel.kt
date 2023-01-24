package com.sgether.ui.room

import android.app.Application
import android.content.Context
import android.os.Build.VERSION_CODES.M
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.models.MemberData
import com.sgether.networks.MyPeerManager
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.EglBase
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer

class RoomViewModel(application: Application): AndroidViewModel(application) {

    private val _memberDataLists = mutableListOf(
        MemberData("홍길동찐", "", isLocal = true),
        MemberData("홍길동", ""),
    )

    private val _memberDataListLiveData = MutableLiveData(_memberDataLists)

    val memberDataListLiveData: LiveData<MutableList<MemberData>>
        get() = _memberDataListLiveData

    fun setMemberDataList(list: MutableList<MemberData>){
        _memberDataListLiveData.value = list
    }

    fun addMemberDataList(memberData: MemberData) {
        _memberDataLists.add(memberData)
        _memberDataListLiveData.value = _memberDataLists
    }
}