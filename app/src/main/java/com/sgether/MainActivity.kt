package com.sgether

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sgether.databinding.ActivityMainBinding
import com.sgether.networks.AppSdpObserver
import com.sgether.networks.PeerConnectionObserver
import com.sgether.networks.PeerManager
import com.sgether.networks.SocketManager
import com.sgether.utils.PermissionHelper
import io.socket.emitter.Emitter
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = ".MainActivity"
        val sdpMid = hashMapOf(
            "audio" to "0",
            "video" to "1",
        )
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var socketManager: SocketManager
    private lateinit var  peerManager: PeerManager

    private var remoteVideoTrack: VideoTrack? = null

    private var answerReceived = false

    private val permissions = listOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
    )
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if(!result.containsValue(false)){
                initSurfaceView()
                peerManager.startLocalSurface(this, binding.surfaceLocal)
            }
        }

    private lateinit var room: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        socketManager = SocketManager(onWelcomeListener, onOfferListener, onAnswerListener, onIceCandidate)
        peerManager = PeerManager(this, peerConnectionObserver)

        PermissionHelper.getDeniedPermissions(this, permissions).run {
            permissionLauncher.launch(this)
        }

        binding.btnStart.setOnClickListener {
            room = binding.inputRoom.text.toString()
            socketManager.joinRoom(room)
        }
    }

    private fun initSurfaceView(){
        peerManager.run {
            initSurfaceView(binding.surfaceLocal)
            initSurfaceView(binding.surfaceRemote)
        }
    }

    private fun sendOffer(){
        peerManager.createOffer(object : AppSdpObserver() {
            override fun onCreateSuccess(sdp: SessionDescription?) {
                peerManager.setLocalDescription(object : AppSdpObserver() {
                    override fun onSetSuccess() {
                        socketManager.sendOffer(sdp, room)
                        Log.d(TAG, "WEBRTC: OFFER 생성 및 전송")
                    }
                }, sdp)
            }
        })
    }

    private val peerConnectionObserver = object : PeerConnectionObserver() {
        override fun onIceCandidate(iceCandidate: IceCandidate?) {
            Log.d(TAG, "WEBRTC: ICE 생성 및 전송")
            iceCandidate?.run {
                Log.d(MainActivity.TAG, "WEBRTC: sdp $sdp")
                Log.d(MainActivity.TAG, "WEBRTC: sdpMid $sdpMid")
                Log.d(MainActivity.TAG, "WEBRTC: sdpMLineIndex $sdpMLineIndex")
            }
            socketManager.sendIce(iceCandidate, room)
        }

        override fun onAddStream(p0: MediaStream?) {
            toast("WEBRTC: RemoteStream 추가 ${p0?.videoTracks?.get(0)?.id()}")
            runOnUiThread {
                p0?.videoTracks?.get(0)?.addSink(binding.surfaceRemote)
            }
        }
    }

    private val onWelcomeListener = Emitter.Listener {
        toast("Received Welcome!")

        sendOffer()
    }

    private val onOfferListener = Emitter.Listener {
        Log.d(TAG, "WEBRTC: OFFER 수신")
        val answer = JSONObject(it[0].toString())
        Log.d(TAG, "WEBRTC: ${it[0]}")
        toast("OFFER 수신")
        // 승낙
        val sdp = SessionDescription(
            SessionDescription.Type.OFFER,
            answer.get("sdp").toString()
        )

        peerManager.setRemoteDescription(object: AppSdpObserver(){
            override fun onSetSuccess() {
                Log.d(TAG, "WEBRTC: OFFER RemoteDescription 설정")
                createAnswer()
            }

            override fun onSetFailure(s: String?) {
                toast(s?:"NULL")
            }
        }, sdp)


    }

    private val onAnswerListener = Emitter.Listener {
        Log.d(TAG, "WEBRTC: ANSWER 수신")
        val answer = JSONObject(it[0].toString())
        Log.d(TAG, "WEBRTC: ${it[0]}")

        val sdp = SessionDescription(
            SessionDescription.Type.ANSWER,
            answer.get("sdp").toString()
        )
        peerManager.setRemoteDescription(object: AppSdpObserver() {
            override fun onSetSuccess() {
                Log.d(TAG, "WEBRTC: ANSWER RemoteDescription 설정")
                answerReceived = true
            }
        }, sdp)
    }

    private fun createAnswer(){
        peerManager.createAnswer(object: AppSdpObserver(){
            override fun onCreateSuccess(sdp: SessionDescription?) {
                Log.d(TAG, "WEBRTC: ANSWER 생성")
                peerManager.setLocalDescription(object: AppSdpObserver(){
                    override fun onSetSuccess() {
                        Log.d(TAG, "WEBRTC: ANSWER LocalDescription 설정")
                        socketManager.sendAnswer(sdp, room)
                        Log.d(TAG, "WEBRTC: ANSWER 전송")
                    }
                }, sdp)

            }
        })
    }


    private val onIceCandidate = Emitter.Listener {
        val ice = JSONObject(it[0].toString())
        Log.d(TAG, "WEBRTC: ICE 수신")

        val sdp = if(ice.has("sdp")){ ice.get("sdp").toString()} else ice.get("candidate").toString()

        //val sdpMid = sdpMid.get(ice.get("sdpMid").toString())
        //Log.d(TAG, "WEBRTC: $sdpMid")
        if(ice.has("candidate")){
            val temp = IceCandidate(
                ice.get("sdpMid").toString(),
                ice.get("sdpMLineIndex").toString().toInt(),
                sdp
            )
            peerManager.addIceCandidate(temp)
        }
    }

    private fun toast(msg: String) {
        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager.close()
    }
}