package com.sgether.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sgether.db.AppDatabase
import com.sgether.models.GroupSearchLog
import com.sgether.networks.response.group.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val _groupList: List<Room> = listOf(

    )

    private val _groupLiveData = MutableLiveData(_groupList)

    val groupLiveData: LiveData<List<Room>>
        get() = _groupLiveData

    private fun setGroupList(list: List<Room>) {
        _groupLiveData.value = list
    }

    fun filterKeywords(keyword: String) {
        setGroupList(_groupList.filter { it.room_name?.contains(keyword) ?: false })
    }

    private val groupSearchLogDao = AppDatabase.getInstance(application).groupSearchLogDao

    fun insertGroupSearchLog(groupSearchLog: GroupSearchLog) =
        viewModelScope.launch(Dispatchers.IO) {
            groupSearchLogDao.insert(groupSearchLog)
        }

    fun deleteGroupSearchLog(groupSearchLog: GroupSearchLog) =
        viewModelScope.launch(Dispatchers.IO) {
            groupSearchLogDao.delete(groupSearchLog)
        }

    val groupSearchLogListLiveData = groupSearchLogDao.readAll()
}