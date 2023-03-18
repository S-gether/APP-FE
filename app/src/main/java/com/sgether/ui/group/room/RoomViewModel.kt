package com.sgether.ui.group.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.sgether.api.ApiClient
import com.sgether.api.request.study.CreateStudyTimeBody
import com.sgether.model.LiveDataResult
import com.sgether.model.MemberData
import com.sgether.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class RoomViewModel(application: Application): AndroidViewModel(application) {

    private val _memberDataList = mutableListOf(
        MemberData(Constants.TYPE_JOIN, "자신", "", isLocal = true),
    )

    val memberDataList: List<MemberData> get() = _memberDataList

    private val _memberDataListLiveData = MutableLiveData(_memberDataList)

    val memberDataListLiveData: LiveData<MutableList<MemberData>>
        get() = _memberDataListLiveData

    fun setMemberDataList(list: MutableList<MemberData>){
        _memberDataListLiveData.postValue(list)
    }

    fun addMemberDataList(memberData: MemberData) {
        _memberDataList.add(memberData)
        _memberDataListLiveData.postValue(_memberDataList)
    }

    fun removeMemberDataList(remoteSocketId: String) {
        val temp = _memberDataList.find {
            it.socketId == remoteSocketId
        }
        _memberDataList.remove(temp)
        _memberDataListLiveData.postValue(_memberDataList)
    }

    private var _createStudyTimeLiveData: MutableLiveData<LiveDataResult>  = MutableLiveData()
    val createStudyTimeLiveData: LiveData<LiveDataResult>
        get() {
            return _createStudyTimeLiveData
        }

    fun createStudyTime(groupId: String, studyTime: Long, aiCount: Int) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val res = ApiClient.studyService.createStudyTime(groupId, CreateStudyTimeBody(studyTime, aiCount))
            if(res.isSuccessful) {
                _createStudyTimeLiveData.postValue(LiveDataResult(true))
            } else {
                _createStudyTimeLiveData.postValue(LiveDataResult(false, res.errorBody()?.string()))
            }
        } catch(e: IOException) {
            _createStudyTimeLiveData.postValue(LiveDataResult(false, e.message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}