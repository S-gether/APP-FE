package com.sgether.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.networks.response.group.Room

class MyGroupViewModel : ViewModel() {
    private val _groupList: List<Room> = listOf()

    private val _groupLiveData = MutableLiveData(_groupList)

    val groupLiveData: LiveData<List<Room>>
        get() = _groupLiveData

    fun setGroupList(list: List<Room>) {
        _groupLiveData.postValue(list)
    }
}