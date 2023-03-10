package com.sgether.ui.group.room

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sgether.adapter.MemberVideoAdapter
import com.sgether.databinding.ActivityRoomBinding
import com.sgether.model.GroupModel
import com.sgether.model.MemberData
import com.sgether.webrtc.SocketManager
import com.sgether.util.Constants
import com.sgether.webrtc.MyPeerManager
import com.sgether.webrtc.observer.AppSdpObserver
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject
import org.webrtc.*

class RoomActivity : AppCompatActivity() {

    private val TAG = ".RoomActivity"

    private val binding by lazy { ActivityRoomBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<RoomViewModel>()

    private lateinit var groupModel: GroupModel

    private val peerManager by lazy { MyPeerManager(this) }
    private val socketManager by lazy {
        SocketManager(
            onJoinListener,
            onOfferListener,
            onAnswerListener,
            onIceCandidate,
            onLeaveRoomListener,
        )
    }

    private val memberVideoAdapter by lazy {
        MemberVideoAdapter(
            nickName,
            peerManager,
            socketManager
        )
    }

    var roomName = "1"
    val nickName = "emulator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        initViews()
        initViewListeners()
        initViewModelListeners()
    }

    private fun init() {
        groupModel = intent?.getParcelableExtra(Constants.KEY_GROUP_MODEL)!!
        roomName = groupModel.room_name?:"NULL"
        socketManager.joinRoom(roomName, nickName)
    }

    private fun initViews() {
        binding.rvMemberVideo.adapter = memberVideoAdapter
    }

    private fun initViewListeners() {
        binding.btnLeave.setOnClickListener {

            socketManager.disconnect()
            peerManager.localStream.videoTracks.forEach {
                it.dispose()
            }
            peerManager.localStream.audioTracks.forEach {
                it.dispose()
            }
            finish()
        }
    }

    private fun initViewModelListeners() {
        viewModel.memberDataListLiveData.observe(this) {
            memberVideoAdapter.list = it
        }
    }

    private val onJoinListener = Emitter.Listener {
        val userList = JSONArray(it[0].toString())
        Log.d(TAG, "onJoinListener: $userList")
        // ?????? ????????? 1?????? ?????? ?????? (??????)
        if (userList.length() == 1) {
            return@Listener
        }

        //socketUserList.clear()
        for (i in 0 until userList.length()) {
            val user = userList.getJSONObject(i)
            Log.d(TAG, "onJoinListener: $user")


            if (user.get("nickname") != nickName) {
                val member = MemberData(
                    Constants.TYPE_JOIN,
                    user.get("nickname").toString(),
                    user.get("socketId").toString(),
                )
                viewModel.addMemberDataList(member)
            }
        }
    }

    private val onOfferListener = Emitter.Listener {
        val offer = JSONObject(it[0].toString())
        val remoteSocketId = it[1].toString()
        val remoteNickName = it[2].toString()
        Log.d(TAG, "onOfferListener: $remoteNickName")

        // Offer ??????
        val sdp = SessionDescription(
            SessionDescription.Type.OFFER,
            offer.get("sdp").toString()
        )

        // PeerConnection ??????
        val member = MemberData(
            Constants.TYPE_OFFER,
            remoteNickName,
            remoteSocketId,
            sdp,
        )
        viewModel.addMemberDataList(member)
    }

    private val onAnswerListener = Emitter.Listener {
        val answer = JSONObject(it[0].toString())
        val remoteSocketId = it[1].toString()
        Log.d(TAG, "onAnswerListener: $remoteSocketId")
        val sdp = SessionDescription(
            SessionDescription.Type.ANSWER,
            answer.get("sdp").toString()
        )

        val peerConnection = getPeerConnection(remoteSocketId)

        peerManager.setRemoteDescription(peerConnection, object : AppSdpObserver() {
            override fun onSetSuccess() {
                Log.d(TAG, "WEBRTC: ANSWER RemoteDescription ??????")
            }

            override fun onSetFailure(s: String?) {
                Log.d(TAG, "WEBRTC: ANSWER RemoteDescription ??????")
            }
        }, sdp)
    }

    private val onIceCandidate = Emitter.Listener {
        val ice = JSONObject(it[0].toString())

        val remoteSocketId = it[1].toString()
        val peerConnection = getPeerConnection(remoteSocketId)

        val sdp = if (ice.has("sdp"))
            ice.get("sdp").toString()
        else ice.get("candidate").toString()

        if (ice.has("candidate") || ice.has("sdp")) {
            val temp = IceCandidate(
                ice.get("sdpMid").toString(),
                ice.get("sdpMLineIndex").toString().toInt(),
                sdp
            )
            peerManager.addIceCandidate(peerConnection, temp)
        }
    }

    private val onLeaveRoomListener = Emitter.Listener {
        val remoteSocketId = it[0].toString()
        viewModel.removeMemberDataList(remoteSocketId)
    }

    private fun getPeerConnection(remoteSocketId: String): PeerConnection? {
        return viewModel.memberDataList.find { memberData ->
            memberData.socketId == remoteSocketId
        }?.peerConnection
    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager.disconnect()
    }
}