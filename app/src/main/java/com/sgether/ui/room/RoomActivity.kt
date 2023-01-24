package com.sgether.ui.room

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sgether.adapters.MemberVideoAdapter
import com.sgether.databinding.ActivityRoomBinding
import com.sgether.models.MemberData
import com.sgether.networks.MyPeerManager
import com.sgether.networks.PeerConnectionObserver
import com.sgether.networks.SocketManager
import io.socket.emitter.Emitter
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnectionDependencies

class RoomActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRoomBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<RoomViewModel>()

    private val peerManager by lazy { MyPeerManager(this) }
    private lateinit var socketManager: SocketManager

    private val memberVideoAdapter by lazy { MemberVideoAdapter(peerManager) }

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
    }

    private fun initViews() {
        binding.rvMemberVideo.adapter = memberVideoAdapter
    }

    private fun initViewModelListeners() {
        viewModel.memberDataListLiveData.observe(this) {
            memberVideoAdapter.list = it
        }
    }

    private fun createMember(remoteSocketId: String): MemberData {
        val memberData = MemberData("홍길동", remoteSocketId).apply {
            peerConnection = peerManager.buildPeerConnection(this@RoomActivity, object : PeerConnectionObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    // ICE 전송
                }

                override fun onAddStream(p0: MediaStream?) {
                    mediaStream = p0
                }
            })
        }
        return memberData
    }


    private val onJoinListener = Emitter.Listener {

    }

    private val onOfferListener = Emitter.Listener {

    }

    private val onAnswerListener = Emitter.Listener {

    }

    private val onIceCandidate = Emitter.Listener {

    }

}