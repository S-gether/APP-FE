package com.sgether.ui.notice

import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.models.Comment

class NoticeViewModel: ViewModel() {

    private val _commentList = mutableListOf(
        Comment("홍길동", "안녕")
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