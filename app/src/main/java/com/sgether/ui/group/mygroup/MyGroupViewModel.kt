package com.sgether.ui.group.mygroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgether.api.ApiClient
import com.sgether.model.GroupIdModel
import com.sgether.model.GroupModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

class MyGroupViewModel : ViewModel() {

    private var _groupList: List<GroupModel> = listOf()
    private val _groupLiveData = MutableLiveData(_groupList)
    val groupLiveData: LiveData<List<GroupModel>>
        get() = _groupLiveData

    fun loadMyGroupList() = viewModelScope.launch(Dispatchers.IO) {
        val groupIdList = async { loadMyGroupIdList() }
        val groupList = async { loadGroupList() }

        _groupList = groupList.await()?.filter { groupModel ->
            groupIdList.await()
                ?.map { it.group_id }
                ?.contains(groupModel.id)
                ?:false
        }?: listOf()

        _groupLiveData.postValue(_groupList)
    }

    private suspend fun loadGroupList(): List<GroupModel>?  {
        try {
            val res = ApiClient.groupService.readGroup()
            val body = res.body()
            if(res.isSuccessful) {
                return body?.groupsSelectReseult
            }
        } catch(e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private suspend fun loadMyGroupIdList(): List<GroupIdModel>?  {
        try {
            val res = ApiClient.joinGroupService.loadMyGroupIdList()
            if(res.isSuccessful) {
                return res.body()?.groupsSelectReseult!!
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}