package com.sgether.ui.group.mygroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.model.GroupModel

class MyGroupViewModel : ViewModel() {
    private val _groupList: List<GroupModel> = listOf(
        // TODO: API 요청 실패 대비용 테스트 그룹
        GroupModel("group_id", "master_id", "sample_room", 5, "sample_pwd", 0, "created_at", "updated_at")
    )

    private val _groupLiveData = MutableLiveData(_groupList)

    val groupLiveData: LiveData<List<GroupModel>>
        get() = _groupLiveData

    fun setGroupList(list: List<GroupModel>) {
        _groupLiveData.postValue(list)
    }
}