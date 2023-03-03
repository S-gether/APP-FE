package com.sgether.ui.group.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sgether.model.MemberData
import com.sgether.util.Constants

class RoomViewModel(application: Application): AndroidViewModel(application) {

    private val _memberDataList = mutableListOf(
        MemberData(Constants.TYPE_JOIN, "본인이름", "", isLocal = true),
    )

    val memberDataList: List<MemberData> get() = _memberDataList

    private val _memberDataListLiveData = MutableLiveData(_memberDataList)

    val memberDataListLiveData: LiveData<MutableList<MemberData>>
        get() = _memberDataListLiveData

    fun setMemberDataList(list: MutableList<MemberData>){
        _memberDataListLiveData.postValue(list)
    }

    fun addMemberDataList(memberData: MemberData) {
        _memberDataList.add(memberData)
        _memberDataListLiveData.postValue(_memberDataList)
    }

    fun removeMemberDataList(remoteSocketId: String) {
        val temp = _memberDataList.find {
            it.socketId == remoteSocketId
        }
        _memberDataList.remove(temp)
        _memberDataListLiveData.postValue(_memberDataList)
    }
}