package com.sgether.ui.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.models.MemberData

class RoomViewModel: ViewModel() {
    private val _memberDataLists = listOf(
        MemberData("홍길동"),
        MemberData("홍길동"),
        MemberData("홍길동"),
        MemberData("홍길동"),
        MemberData("홍길동"),
        MemberData("홍길동"),
        MemberData("홍길동"),
        MemberData("홍길동"),
        MemberData("홍길동"),
    )

    private val _memberDataListLiveData = MutableLiveData(_memberDataLists)

    val memberDataListLiveData: LiveData<List<MemberData>>
        get() = _memberDataListLiveData

    private fun setMemberDataList(list: List<MemberData>){
        _memberDataListLiveData.value = list
    }
}