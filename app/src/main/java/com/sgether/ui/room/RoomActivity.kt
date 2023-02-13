package com.sgether.ui.room

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sgether.adapters.MemberVideoAdapter
import com.sgether.databinding.ActivityRoomBinding
import com.sgether.models.MemberData
import com.sgether.webrtc.observer.AppSdpObserver
import com.sgether.webrtc.MyPeerManager
import com.sgether.webrtc.observer.PeerConnectionObserver
import com.sgether.networks.SocketManager
import com.sgether.utils.Constants
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject
import org.webrtc.*

class RoomActivity : AppCompatActivity() {

    private val TAG = ".RoomActivity"

    private val binding by lazy { ActivityRoomBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<RoomViewModel>()

    private val peerManager by lazy { MyPeerManager(this) }
    private val socketManager by lazy { SocketManager(onJoinListener, onOfferListener, onAnswerListener, onIceCandidate) }

    private val memberVideoAdapter by lazy { MemberVideoAdapter(nickName, peerManager, socketManager) }

    val roomName = "1"
    val nickName = "phone"

    var socketUserList = mutableListOf<MemberData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        initViews()
        initViewModelListeners()
    }

    private fun init() {
        socketManager.joinRoom(roomName, nickName)
    }

    private fun initViews() {
        binding.rvMemberVideo.adapter = memberVideoAdapter
    }

    private fun initViewModelListeners() {
        viewModel.memberDataListLiveData.observe(this) {
            memberVideoAdapter.list = it
        }
    }

    private val onJoinListener = Emitter.Listener {
        val userList = JSONArray(it[0].toString())
        Log.d(TAG, "onJoinListener: $userList")
        // 유저 목록이 1명일 경우 종료 (자신)
        if(userList.length() == 1){
            return@Listener
        }

        socketUserList.clear()
        for(i in 0 until userList.length()){
            val user = userList.getJSONObject(i)
            Log.d(TAG, "onJoinListener: $user")


            if(user.get("nickname") != nickName) {
                val member = MemberData(Constants.TYPE_JOIN,
                    user.get("nickname").toString(),
                    user.get("socketId").toString(),
                )
                socketUserList.add(member)
                Log.d(TAG, "Add member: ${socketUserList.size}")
            }
        }
        viewModel.setMemberDataList(socketUserList.apply {
            add(0, MemberData(Constants.TYPE_JOIN, "나", "", isLocal = true))
        })
    }

    private val onOfferListener = Emitter.Listener {
        val offer = JSONObject(it[0].toString())
        val remoteSocketId = it[1].toString()
        val remoteNickName = it[2].toString()
        Log.d(TAG, "onOfferListener: $remoteNickName")

        // Offer 변환
        val sdp = SessionDescription(
            SessionDescription.Type.OFFER,
            offer.get("sdp").toString()
        )

        // PeerConnection 생성
        val member = MemberData(
            Constants.TYPE_OFFER,
            remoteNickName,
            remoteSocketId,
            sdp,
        )
        socketUserList.add(member)
        viewModel.setMemberDataList(socketUserList.apply {
            add(0, MemberData(Constants.TYPE_JOIN, "홍길동찐", "", isLocal = true))
        })
    }

    private val onAnswerListener = Emitter.Listener {
        val answer = JSONObject(it[0].toString())
        val remoteSocketId = it[1].toString()
        Log.d(TAG, "onAnswerListener: $remoteSocketId")
        val sdp = SessionDescription(
            SessionDescription.Type.ANSWER,
            answer.get("sdp").toString()
        )

        Log.d(TAG, ": ${socketUserList.size}")
        val peerConnection = getPeerConnection(remoteSocketId)

        peerManager.setRemoteDescription(peerConnection, object: AppSdpObserver() {
            override fun onSetSuccess() {
                Log.d(TAG, "WEBRTC: ANSWER RemoteDescription 설정")
            }

            override fun onSetFailure(s: String?) {
                Log.d(TAG, "WEBRTC: ANSWER RemoteDescription 실패")
            }
        }, sdp)
    }

    private val onIceCandidate = Emitter.Listener {
        val ice = JSONObject(it[0].toString())

        val remoteSocketId = it[1].toString()
        val peerConnection = getPeerConnection(remoteSocketId)

        val sdp = if(ice.has("sdp"))
            ice.get("sdp").toString()
        else ice.get("candidate").toString()

        if(ice.has("candidate") || ice.has("sdp")){
            val temp = IceCandidate(
                ice.get("sdpMid").toString(),
                ice.get("sdpMLineIndex").toString().toInt(),
                sdp
            )
            peerManager.addIceCandidate(peerConnection, temp)
        }
    }

    private fun getPeerConnection(remoteSocketId: String): PeerConnection?{
        return  socketUserList.find { memberData ->
            memberData.socketId == remoteSocketId
        }?.peerConnection
    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager.disconnect()
    }

    interface onAddStreamListener {
        fun onAdd()
    }
}