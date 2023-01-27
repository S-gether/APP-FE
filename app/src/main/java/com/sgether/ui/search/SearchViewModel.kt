package com.sgether.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.models.Group

class SearchViewModel: ViewModel() {
    private val _groupList = listOf(
        Group("테스트 그룹11", "안녕하세요!"),
        Group("테스트 그룹12", "안녕하세요!"),
        Group("테스트 그룹13", "안녕하세요!"),
        Group("테스트 그룹24", "안녕하세요!"),
        Group("테스트 그룹25", "안녕하세요!"),
        Group("테스트 그룹26", "안녕하세요!"),
        Group("테스트 그룹37", "안녕하세요!"),
        Group("테스트 그룹38", "안녕하세요!"),
    )

    private val _groupLiveData = MutableLiveData(_groupList)

    val groupLiveData: LiveData<List<Group>>
        get() = _groupLiveData

    private fun setGroupList(list: List<Group>){
        _groupLiveData.value = list
    }

    fun filterKeywords(keyword: String) {
        setGroupList(_groupList.filter { it.name.contains(keyword) })
    }
}