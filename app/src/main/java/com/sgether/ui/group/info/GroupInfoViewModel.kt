package com.sgether.ui.group.info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgether.api.ApiClient
import com.sgether.model.GroupModel
import com.sgether.model.LiveDataResult
import com.sgether.model.MemberRanking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class GroupInfoViewModel : ViewModel() {

    lateinit var groupModel: GroupModel

    private var _memberRankingLists: List<MemberRanking> = listOf()
    private val _memberRankingListLiveData = MutableLiveData(_memberRankingLists)
    val memberRankingListLiveData: LiveData<List<MemberRanking>>
        get() = _memberRankingListLiveData

    fun loadGroupMember(groupId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val res = ApiClient.groupService.readGroupUsers(groupId)
            if(res.isSuccessful) {
                val userInfo = res.body()?.usersInfo
                Log.d(null, "loadGroupMember: $userInfo")
                _memberRankingListLiveData.postValue(userInfo?.map {
                    MemberRanking(it.name, it.introduce, "https://cdn.pixabay.com/photo/2023/03/07/11/58/woman-7835587_1280.jpg", 1678445743, it.user_id == groupModel.master_id)
                }?: listOf())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // JoinGroup
    private val _joinGroupResult: MutableLiveData<LiveDataResult> = MutableLiveData()
    val joinGroupResult: LiveData<LiveDataResult> get() = _joinGroupResult

    fun joinGroup(groupId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val res = ApiClient.joinGroupService.joinGroup(groupId)
            if(res.isSuccessful){
                // 성공 결과 반환
                _joinGroupResult.postValue(LiveDataResult(true))
            } else {
                // TODO: Api 실패 결과 문서 정보 부족해서 다음에 처리함
                _joinGroupResult.postValue(LiveDataResult(false))
            }
        } catch (e: Exception) {
            // TODO: 에러 처리
        }
    }
}