package com.sgether.ui.group.room

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sgether.adapter.MemberVideoAdapter
import com.sgether.databinding.ActivityRoomBinding
import com.sgether.model.GroupModel
import com.sgether.model.MemberData
import com.sgether.util.Constants
import com.sgether.util.JWTHelper
import com.sgether.webrtc.MyPeerManager
import com.sgether.webrtc.SocketManager
import com.sgether.webrtc.observer.AppSdpObserver
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.webrtc.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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

    private val module by lazy {
        LiteModuleLoader.load(assetFilePath(applicationContext, "model.ptl"))
    }

    private fun assetFilePath(context: Context, assetName: String): String? {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        try {
            context.assets.open(assetName).use { `is` ->
                FileOutputStream(file).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (`is`.read(buffer).also { read = it } != -1) {
                        os.write(buffer, 0, read)
                    }
                    os.flush()
                }
                return file.absolutePath
            }
        } catch (e: IOException) {
            Log.e(TAG, assetName + ": " + e.getLocalizedMessage())
        }
        return null
    }

    private val memberVideoAdapter by lazy {
        MemberVideoAdapter(
            nickName,
            peerManager,
            socketManager,
            module
        )
    }

    var roomName = "1"
    var nickName = "emulator"
    
    private var startTime = 0L
    private var aiCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        initViews()
        initViewListeners()
        initViewModelListeners()
    }

    private fun init() {
        startTime = System.currentTimeMillis() // 현재 시간을 얻어옴

        val payload: JWTHelper.JwtPayload? = intent.getParcelableExtra(Constants.JWT_PAYLOAD)
        nickName = payload?.name?:"NULL"

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
        // 유저 목록이 1명일 경우 종료 (자신)
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

        val sdp = if (ice.has("sdp"))
            ice.get("sdp").toString()
        else ice.get("candidate").toString()
        Log.d(null, "onIceCandidate: ")
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

        val studyTime = System.currentTimeMillis() - startTime
        viewModel.createStudyTime(groupModel.id?:"", studyTime, aiCount)
    }
}