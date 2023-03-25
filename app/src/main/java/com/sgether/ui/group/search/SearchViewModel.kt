package com.sgether.ui.group.search

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sgether.db.AppDatabase
import com.sgether.model.GroupModel
import com.sgether.model.GroupSearchLog
import com.sgether.api.ApiClient
import com.sgether.util.toastOnMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    // 그룹 목록
    private var _groupList = mutableListOf<GroupModel>()

    private val _groupLiveData: MutableLiveData<List<GroupModel>> = MutableLiveData(_groupList)
    val groupLiveData: LiveData<List<GroupModel>>
        get() = _groupLiveData

    // 리사이클러뷰 전송
    private fun setGroupList(list: List<GroupModel>) {
        _groupList = list.toMutableList()
        _groupLiveData.postValue(_groupList)
    }

    fun filterKeywords(keyword: String) {
        _groupLiveData.postValue(_groupList.filter { it.room_name?.contains(keyword) ?: false })
    }

    fun removeFilter(){
        setGroupList(_groupList)
    }

    fun loadGroupList() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val result = ApiClient.groupService.readGroup()
            if (result.isSuccessful) {
                val body = result.body()
                setGroupList(body?.groupsSelectReseult!!)

            } else {
                val errorBody = ApiClient.parseErrorBody(result.errorBody())
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), errorBody?.message, Toast.LENGTH_SHORT).show()
                }
            }
        } catch(e: IOException) {
            e.printStackTrace()
        }
    }
}