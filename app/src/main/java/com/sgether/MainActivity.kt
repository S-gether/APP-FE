package com.sgether

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sgether.databinding.ActivityMainBinding
import com.sgether.networks.AppSdpObserver
import com.sgether.networks.PeerConnectionObserver
import com.sgether.networks.PeerManager
import com.sgether.networks.SocketManager
import com.sgether.utils.PermissionHelper
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = ".MainActivity"
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var socketManager: SocketManager
    private lateinit var  peerManager: PeerManager

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if(!result.containsValue(false)){
                initSurfaceView()
                peerManager.startLocalSurface(this, binding.surfaceLocal)
            }
        }

    private lateinit var room: String

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var isReady = false
        Timer("CheckLogin").schedule(1000){
            isReady = true
        }
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object: ViewTreeObserver.OnPreDrawListener{
                override fun onPreDraw(): Boolean {
                    return if(isReady){
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )

        socketManager = SocketManager(onWelcomeListener, onOfferListener, onAnswerListener, onIceCandidate)
        peerManager = PeerManager(this, peerConnectionObserver)

        // 권한 부여
        PermissionHelper.getDeniedPermissions(this, listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
        )).run {
            permissionLauncher.launch(this)
        }

        initViewListeners()
    }

    private fun initViewListeners(){
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

    private val peerConnectionObserver = object : PeerConnectionObserver() {
        override fun onIceCandidate(iceCandidate: IceCandidate?) {
            Log.d(TAG, "WEBRTC: ICE 생성 및 전송")
            socketManager.sendIce(iceCandidate, room)
        }

        override fun onAddStream(p0: MediaStream?) {
            CoroutineScope(Dispatchers.Main).launch {
                p0?.videoTracks?.get(0)?.addSink(binding.surfaceRemote)
            }
        }
    }

    private val onWelcomeListener = Emitter.Listener {
        sendOffer()
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

    private val onOfferListener = Emitter.Listener {
        Log.d(TAG, "WEBRTC: OFFER 수신")
        val offer = JSONObject(it[0].toString())

        val sdp = SessionDescription(
            SessionDescription.Type.OFFER,
            offer.get("sdp").toString()
        )

        peerManager.setRemoteDescription(object: AppSdpObserver(){
            override fun onSetSuccess() {
                Log.d(TAG, "WEBRTC: OFFER RemoteDescription 설정")
                createAnswer()
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

    private val onAnswerListener = Emitter.Listener {
        Log.d(TAG, "WEBRTC: ANSWER 수신")
        val answer = JSONObject(it[0].toString())

        val sdp = SessionDescription(
            SessionDescription.Type.ANSWER,
            answer.get("sdp").toString()
        )

        peerManager.setRemoteDescription(object: AppSdpObserver() {
            override fun onSetSuccess() {
                Log.d(TAG, "WEBRTC: ANSWER RemoteDescription 설정")
            }
        }, sdp)
    }




    private val onIceCandidate = Emitter.Listener {
        val ice = JSONObject(it[0].toString())
        Log.d(TAG, "WEBRTC: ICE 수신")

        val sdp = if(ice.has("sdp"))
                ice.get("sdp").toString()
            else ice.get("candidate").toString()

        if(ice.has("candidate") || ice.has("sdp")){
            val temp = IceCandidate(
                ice.get("sdpMid").toString(),
                ice.get("sdpMLineIndex").toString().toInt(),
                sdp
            )
            peerManager.addIceCandidate(temp)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager.close()
    }
}