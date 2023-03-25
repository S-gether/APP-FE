package com.sgether.ui.group.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.model.Comment

class NoticeViewModel: ViewModel() {

    private val _commentList = mutableListOf(
        Comment("1","홍길동", "Hello guys! This is a notice board for Erpingchun Elementry school 3rd grade class 7"),
        Comment("1","홍길동", "There was an error in the assignment on page 17 of the Korean language book today, so the teacher told me not to do it!"),
        Comment("1","홍길동", "안321313123"),
        Comment("1","홍길동", "안녕2133"),
        Comment("1","홍길동", "안녕2131313123"),
        Comment("1","홍길동", "안녕2133123"),
        Comment("1","홍길동", "안녕2132131fdsf sfsfdfdsfds3123"),
    )

    val commentList: List<Comment>
        get() = _commentList

    private val _commentListLiveData = MutableLiveData(commentList)
    val commentListLiveData: LiveData<List<Comment>>
        get() = _commentListLiveData

    fun addComment(comment: Comment){
        _commentList.add(comment)
        _commentListLiveData.value = _commentList
    }
}