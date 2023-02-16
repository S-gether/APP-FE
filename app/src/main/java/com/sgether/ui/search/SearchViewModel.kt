package com.sgether.ui.search

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sgether.db.AppDatabase
import com.sgether.models.GroupSearchLog
import com.sgether.networks.RetrofitHelper
import com.sgether.networks.response.group.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    init {
        loadGroupList()
    }

    // 그룹 목록
    private var _groupList = mutableListOf<Room>()

    private val _groupLiveData: MutableLiveData<List<Room>> = MutableLiveData(_groupList)
    val groupLiveData: LiveData<List<Room>>
        get() = _groupLiveData

    // 리사이클러뷰 전송
    private fun setGroupList(list: List<Room>) {
        _groupList = list.toMutableList()
        _groupLiveData.postValue(_groupList)
    }

    fun filterKeywords(keyword: String) {
        _groupLiveData.postValue(_groupList.filter { it.room_name?.contains(keyword) ?: false })
    }

    private fun loadGroupList() = viewModelScope.launch(Dispatchers.IO) {
        val result = RetrofitHelper.groupService.readGroup()
        if(result.isSuccessful) {
            val body = result.body()
            setGroupList(body?.groupsSelectReseult!!)

        } else {
            val errorBody = RetrofitHelper.parseErrorBody(result.errorBody())
            withContext(Dispatchers.Main) {
                Toast.makeText(getApplication(), errorBody?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 최근 검색어
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