package com.sgether.ui.group.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgether.api.ApiClient
import com.sgether.model.MemberRanking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupInfoViewModel : ViewModel() {
    private val _memberRankingLists = listOf(
        MemberRanking("홍길동", 1),
        MemberRanking("홍길동", 1),
        MemberRanking("홍길동", 1),
        MemberRanking("홍길동", 1),
        MemberRanking("홍길동", 1),
        MemberRanking("홍길동", 1),
        MemberRanking("홍길동", 1),
        MemberRanking("홍길동", 1),
        MemberRanking("홍길동", 1),
        MemberRanking("홍길동", 1),
        MemberRanking("홍길동", 1),
    )

    private val _memberRankingListLiveData = MutableLiveData(_memberRankingLists)

    val memberRankingListLiveData: LiveData<List<MemberRanking>>
        get() = _memberRankingListLiveData

    private fun setMemberRankingList(list: List<MemberRanking>){
        _memberRankingListLiveData.value = list
    }

    /**
     * 그룹에 참여하는 API를 호출합니다.
     */
    fun joinGroup(groupId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val res = ApiClient.joinGroupService.joinGroup(groupId)
            if(res.code() == 201){

            } else {

            }
        } catch (e: Exception) {

        }
    }
}