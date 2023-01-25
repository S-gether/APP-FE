package com.sgether.ui.room

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sgether.adapters.MemberVideoAdapter
import com.sgether.databinding.ActivityRoomBinding
import com.sgether.models.MemberData
import com.sgether.networks.AppSdpObserver
import com.sgether.networks.MyPeerManager
import com.sgether.networks.PeerConnectionObserver
import com.sgether.networks.SocketManager
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject
import org.webrtc.*

class RoomActivity : AppCompatActivity() {

    private val TAG = ".RoomActivity"

    private val binding by lazy { ActivityRoomBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<RoomViewModel>()

    private val peerManager by lazy { MyPeerManager(this) }
    private lateinit var socketManager: SocketManager

    private val memberVideoAdapter by lazy { MemberVideoAdapter(peerManager) }

    val roomName = "1"
    val nickName = "test"

    var socketUserList = mutableListOf<MemberData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        initViews()
        initViewModelListeners()
    }

    private fun init() {
        socketManager =
            SocketManager(onJoinListener, onOfferListener, onAnswerListener, onIceCandidate)

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

    private fun createMember(remoteSocketId: String, name: String): MemberData {
        val memberData = MemberData(name, remoteSocketId).apply {
            peerConnection = peerManager.buildPeerConnection(this@RoomActivity, object : PeerConnectionObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    socketManager.sendIce(p0, remoteSocketId)
                }

                override fun onAddStream(p0: MediaStream?) {
                    mediaStream = p0
                }
            })
            peerConnection?.addStream(peerManager.localStream)
        }
        return memberData
    }

    private val onJoinListener = Emitter.Listener {
        val userList = JSONArray(it[0].toString())
        val first = userList.getJSONObject(0)


        // 유저 목록이 1명일 경우 종료 (자신)
        if(userList.length() == 1){
            return@Listener
        }

        socketUserList.clear()
        for(i in 0 until userList.length()){
            val user = userList.getJSONObject(i)
            Log.d(TAG, "onJoinListener: $user")


            if(user.get("nickname") != nickName) {
                val member = createMember(
                    user.get("socketId").toString(),
                    user.get("nickname").toString(),
                )
                socketUserList.add(member)
                Log.d(TAG, "Add member: ${socketUserList.size}")
                sendOffer(member.peerConnection, member.socketId)
            }
        }
        viewModel.setMemberDataList(socketUserList.apply {
            add(0, MemberData("홍길동찐", "", isLocal = true))
            add(0, MemberData("홍길동", ""))
            add(0, MemberData("홍길동", ""))
            add(0, MemberData("홍길동", ""))
            add(0, MemberData("홍길동", ""))
            add(0, MemberData("홍길동", ""))
            add(0, MemberData("홍길동", ""))
            add(0, MemberData("홍길동", ""))
            add(0, MemberData("홍길동", ""))
            add(0, MemberData("홍길동", ""))
            add(0, MemberData("홍길동", ""))
            add(0, MemberData("홍길동", ""))

        })
    }

    private fun sendOffer(peerConnection: PeerConnection?, socketId: String) {
        peerManager.createOffer(peerConnection, object : AppSdpObserver() {
            override fun onCreateSuccess(sdp: SessionDescription?) {
                peerManager.setLocalDescription(peerConnection, object : AppSdpObserver() {
                    override fun onSetSuccess() {
                        socketManager.sendOffer(sdp, socketId, nickName)
                    }
                }, sdp)
            }
        })
    }

    private val onOfferListener = Emitter.Listener {

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

}