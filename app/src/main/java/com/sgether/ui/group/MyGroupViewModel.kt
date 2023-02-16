package com.sgether.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.models.GroupModel

class MyGroupViewModel : ViewModel() {
    private val _groupList: List<GroupModel> = listOf()

    private val _groupLiveData = MutableLiveData(_groupList)

    val groupLiveData: LiveData<List<GroupModel>>
        get() = _groupLiveData

    fun setGroupList(list: List<GroupModel>) {
        _groupLiveData.postValue(list)
    }
}