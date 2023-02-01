package com.sgether.ui.notice

import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.models.Comment

class NoticeViewModel: ViewModel() {

    private val _commentList = mutableListOf(
        Comment("1","홍길동", "안녕21321313123"),
        Comment("1","홍길동", "안녕213213"),
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