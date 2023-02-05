package com.sgether.ui.search

import android.app.Application
import androidx.lifecycle.*
import com.sgether.db.AppDatabase
import com.sgether.models.Group
import com.sgether.models.GroupSearchLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(application: Application): AndroidViewModel(application) {
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

    private val groupSearchLogDao = AppDatabase.getInstance(application).groupSearchLogDao

    fun insertGroupSearchLog(groupSearchLog: GroupSearchLog) = viewModelScope.launch(Dispatchers.IO) {
        groupSearchLogDao.insert(groupSearchLog)
    }

    fun deleteGroupSearchLog(groupSearchLog: GroupSearchLog) = viewModelScope.launch(Dispatchers.IO) {
        groupSearchLogDao.delete(groupSearchLog)
    }

    val groupSearchLogListLiveData = groupSearchLogDao.readAll()
}