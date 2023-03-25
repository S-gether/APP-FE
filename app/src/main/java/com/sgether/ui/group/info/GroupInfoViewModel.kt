package com.sgether.ui.group.info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgether.api.ApiClient
import com.sgether.api.response.study.StudyTime
import com.sgether.model.GroupModel
import com.sgether.model.ResultModel
import com.sgether.model.MemberRanking
import com.sgether.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

class GroupInfoViewModel : ViewModel() {

    lateinit var groupModel: GroupModel

    private var _memberRankingLists: List<MemberRanking> = listOf()
    private val _memberRankingListLiveData = MutableLiveData(_memberRankingLists)
    val memberRankingListLiveData: LiveData<List<MemberRanking>>
        get() = _memberRankingListLiveData

    fun loadGroupMemberStudyTime(groupId: String) = viewModelScope.launch(Dispatchers.IO) {
        val groupMembers = async { loadGroupMember(groupId) }
        val groupStudyTime = async { loadGroupStudyTime(groupId) }

        val memberRankingList = groupMembers.await()
            ?.map { user ->
                val step0 = groupStudyTime.await()
                val step1 = step0
                    ?.groupBy { group -> group.user_id }
                val step2 = step1
                    ?.get(user.user_id)
                val step3 = step2
                    ?.sumOf { studyTime -> studyTime.total_time }



                MemberRanking(user.user_id, user.name, user.introduce, "https://cdn.pixabay.com/photo/2023/03/07/11/58/woman-7835587_1280.jpg",
                    step3?:0,
                    groupModel.master_id == user.user_id
                )
            }
        Log.d(null, "loadGroupMemberStudyTime: $memberRankingList")
        _memberRankingListLiveData.postValue(memberRankingList)
    }

    private suspend fun loadGroupMember(groupId: String): List<User>? {
        try {
            val res = ApiClient.groupService.readGroupUsers(groupId)
            if(res.isSuccessful) {
                return res.body()?.usersInfo
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private suspend fun loadGroupStudyTime(groupId: String): List<StudyTime>? {
        try {
            val res = ApiClient.studyService.readGroupStudyTime(groupId)
            if(res.isSuccessful) {
                return res.body()?.groupSelectReseult
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // JoinGroup
    private val _joinGroupResult: MutableLiveData<ResultModel> = MutableLiveData()
    val joinGroupResult: LiveData<ResultModel> get() = _joinGroupResult

    fun joinGroup(groupId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val res = ApiClient.joinGroupService.joinGroup(groupId)
            if(res.isSuccessful){
                // 성공 결과 반환
                _joinGroupResult.postValue(ResultModel(true))
            } else {
                _joinGroupResult.postValue(ResultModel(false))
            }
        } catch (e: Exception) {
        }
        loadGroupMemberStudyTime(groupModel.id!!)
    }

    fun dropGroup(groupId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val res = ApiClient.joinGroupService.dropGroup(groupId)
            if(res.isSuccessful){
                // 성공 결과 반환
                _joinGroupResult.postValue(ResultModel(true))

            } else {
                _joinGroupResult.postValue(ResultModel(false))
            }
        } catch (e: Exception) {
        }
        loadGroupMemberStudyTime(groupModel.id!!)
    }

    val deleteGroupResult = MutableLiveData<ResultModel>()

    fun deleteGroup(roomId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val res = ApiClient.groupService.deleteGroup(roomId)
            if(res.isSuccessful) {
                deleteGroupResult.postValue(ResultModel(true))
            } else {
                deleteGroupResult.postValue(ResultModel(false, res.errorBody()?.string()))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            deleteGroupResult.postValue(ResultModel(false, e.message))
        }
    }
}