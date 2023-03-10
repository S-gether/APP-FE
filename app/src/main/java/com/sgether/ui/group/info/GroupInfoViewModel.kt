package com.sgether.ui.group.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgether.api.ApiClient
import com.sgether.model.LiveDataResult
import com.sgether.model.MemberRanking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupInfoViewModel : ViewModel() {
    private val _memberRankingLists = listOf(
        MemberRanking("홍길동", "자기소개", "https://cdn.pixabay.com/photo/2023/03/07/11/58/woman-7835587_1280.jpg", 1678445743, true),
        MemberRanking("홍길동", "자기소개", "https://cdn.pixabay.com/photo/2023/03/07/11/58/woman-7835587_1280.jpg", 1678445743, false),
        MemberRanking("홍길동", "자기소개", "https://cdn.pixabay.com/photo/2023/03/07/11/58/woman-7835587_1280.jpg", 1678445743, false),
        MemberRanking("홍길동", "자기소개", "https://cdn.pixabay.com/photo/2023/03/07/11/58/woman-7835587_1280.jpg", 1678445743, false),
        MemberRanking("홍길동", "자기소개", "https://cdn.pixabay.com/photo/2023/03/07/11/58/woman-7835587_1280.jpg", 1678445743, false),
        MemberRanking("홍길동", "자기소개", "https://cdn.pixabay.com/photo/2023/03/07/11/58/woman-7835587_1280.jpg", 1678445743, false),
        MemberRanking("홍길동", "자기소개", "https://cdn.pixabay.com/photo/2023/03/07/11/58/woman-7835587_1280.jpg", 1678445743, false),
    )

    private val _memberRankingListLiveData = MutableLiveData(_memberRankingLists)

    val memberRankingListLiveData: LiveData<List<MemberRanking>>
        get() = _memberRankingListLiveData

    private fun setMemberRankingList(list: List<MemberRanking>){
        _memberRankingListLiveData.value = list
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