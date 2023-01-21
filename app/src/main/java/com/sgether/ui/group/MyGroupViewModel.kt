package com.sgether.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.models.Group

class MyGroupViewModel : ViewModel() {
    private val _groupList = listOf(
        Group("테스트 그룹", "안녕하세요!"),
        Group("테스트 그룹", "안녕하세요!"),
        Group("테스트 그룹", "안녕하세요!"),
        Group("테스트 그룹", "안녕하세요!"),
        Group("테스트 그룹", "안녕하세요!"),
        Group("테스트 그룹", "안녕하세요!"),
        Group("테스트 그룹", "안녕하세요!"),
        Group("테스트 그룹", "안녕하세요!"),
    )

    private val _groupLiveData = MutableLiveData(_groupList)

    val groupLiveData: LiveData<List<Group>>
        get() = _groupLiveData

    private fun setGroupList(list: List<Group>){
        _groupLiveData.value = list
    }
}