package com.sgether.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.models.MemberRanking

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
}