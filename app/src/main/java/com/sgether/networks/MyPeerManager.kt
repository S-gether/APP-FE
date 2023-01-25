package com.sgether.networks

import android.content.Context
import com.sgether.R
import org.webrtc.*

class MyPeerManager(context: Context) {
    private val eglBaseContext = EglBase.create().eglBaseContext
    lateinit var localStream: MediaStream

    private val localVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
    private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints()) }

    private val peerConnectionFactory by lazy { buildPeerConnectionFactory(context) }

    private fun buildPeerConnectionFactory(context: Context): PeerConnectionFactory {
        initPeerConnectionFactory(context)
        return PeerConnectionFactory.builder()
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    eglBaseContext,
                    true,
                    true
                )
            )
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBaseContext))
            .createPeerConnectionFactory()
    }

    private fun initPeerConnectionFactory(context: Context) {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()

        PeerConnectionFactory.initialize(options)
    }

    fun startLocalSurface(context: Context, surfaceViewRenderer: SurfaceViewRenderer) {
        val videoCapturer = getVideoCapturer(context)

        val surfaceTextureHelper =
            SurfaceTextureHelper.create(Thread.currentThread().name, eglBaseContext)

        videoCapturer?.initialize(
            surfaceTextureHelper,
            surfaceViewRenderer.context, localVideoSource.capturerObserver
        )

        videoCapturer?.startCapture(320, 240, 30)

        val localVideoTrack =
            peerConnectionFactory.createVideoTrack("local_video", localVideoSource)
        localVideoTrack?.addSink(surfaceViewRenderer)

        val localAudioTrack =
            peerConnectionFactory.createAudioTrack("local_audio", localAudioSource)

        localStream = peerConnectionFactory.createLocalMediaStream("local_stream").apply {
            addTrack(localVideoTrack)
            addTrack(localAudioTrack)
        }
        // 피어로 스트림 전송
        //peerConnection?.addStream(localStream)
    }

    fun initSurfaceView(surfaceViewRenderer: SurfaceViewRenderer) {
        surfaceViewRenderer.run {
            setEnableHardwareScaler(true)
            setMirror(true)
            init(eglBaseContext, null)
        }
    }

    private fun getVideoCapturer(context: Context): CameraVideoCapturer? {
        return Camera2Enumerator(context).run {
            deviceNames.find { isFrontFacing(it) }?.let {
                createCapturer(it, null)
            }
        }
    }

    fun buildPeerConnection(
        context: Context,
        observer: PeerConnection.Observer
    ): PeerConnection? {
        return peerConnectionFactory.createPeerConnection(
            listOf(
                PeerConnection.IceServer.builder(
                    context.resources.getStringArray(R.array.stun_server).toList()
                ).createIceServer(),
            ),
            observer
        )
    }

    fun createOffer(peerConnection: PeerConnection?, sdpObserver: AppSdpObserver){
        val mediaConstraint = MediaConstraints().apply {
            mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo", "true"
                )
            )
        }
        peerConnection?.createOffer(sdpObserver, mediaConstraint)
    }

    fun setLocalDescription(peerConnection: PeerConnection?, observer: AppSdpObserver, sdp: SessionDescription?) {
        peerConnection?.setLocalDescription(observer, sdp)
    }

    fun setRemoteDescription(peerConnection: PeerConnection?, observer: AppSdpObserver, sdp: SessionDescription?){
        peerConnection?.setRemoteDescription(observer, sdp)
    }

    fun addIceCandidate(peerConnection: PeerConnection?, iceCandidate: IceCandidate?){
        peerConnection?.addIceCandidate(iceCandidate)
    }

}