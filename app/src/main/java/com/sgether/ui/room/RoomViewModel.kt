package com.sgether.ui.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sgether.models.MemberData
import com.sgether.utils.Constants

class RoomViewModel(application: Application): AndroidViewModel(application) {

    private val _memberDataLists = mutableListOf(
        MemberData(Constants.TYPE_JOIN, "홍길동찐", "", isLocal = true),
    )

    private val _memberDataListLiveData = MutableLiveData(_memberDataLists)

    val memberDataListLiveData: LiveData<MutableList<MemberData>>
        get() = _memberDataListLiveData

    fun setMemberDataList(list: MutableList<MemberData>){
        _memberDataListLiveData.postValue(list)
    }

    fun addMemberDataList(memberData: MemberData) {
        _memberDataLists.add(memberData)
        _memberDataListLiveData.value = _memberDataLists
    }
}