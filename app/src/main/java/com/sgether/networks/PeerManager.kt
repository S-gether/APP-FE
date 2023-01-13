package com.sgether.networks

import android.content.Context
import org.webrtc.*

class PeerManager(context: Context, peerConnectionObserver: PeerConnection.Observer) {
    private val peerConnectionFactory by lazy { buildPeerConnectionFactory(context) }
    private val eglBase = EglBase.create()

    private val localVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
    private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints()) }

    private fun buildPeerConnectionFactory(context: Context): PeerConnectionFactory {
        initPeerConnectionFactory(context)
        return PeerConnectionFactory.builder()
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    eglBase.eglBaseContext,
                    true,
                    true
                )
            )
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .createPeerConnectionFactory()
    }

    private fun initPeerConnectionFactory(context: Context) {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()

        PeerConnectionFactory.initialize(options)
    }

    private val peerConnection by lazy { buildPeerConnection(peerConnectionObserver) }

    private val iceServers = listOf(
        PeerConnection.IceServer.builder(listOf(
            "stun:stun.l.google.com:19302",
            "stun:stun1.l.google.com:19302",
            "stun:stun2.l.google.com:19302",
            "stun:stun3.l.google.com:19302",
            "stun:stun4.l.google.com:19302",
        )).createIceServer(),
        PeerConnection.IceServer("turn:relay.metered.ca:80","c9499a07e1d9ff4d54bbd443","ME0Ta1JsY2cGAU3Y"),
        PeerConnection.IceServer("turn:relay.metered.ca:443","c9499a07e1d9ff4d54bbd443","ME0Ta1JsY2cGAU3Y"),
    )

    private fun buildPeerConnection(observer: PeerConnection.Observer): PeerConnection? {
        return peerConnectionFactory.createPeerConnection(iceServers, observer)
    }

    fun createOffer(sdpObserver: AppSdpObserver) {
        val mediaConstraint = MediaConstraints().apply {
            mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo", "true"
                )
            )
        }
        peerConnection?.createOffer(sdpObserver, mediaConstraint)
    }

    fun createAnswer(sdpObserver: AppSdpObserver){
        val mediaConstraint = MediaConstraints().apply {
//            mandatory.add(
//                MediaConstraints.KeyValuePair(
//                    "OfferToReceiveVideo", "true"
//                )
//            )
        }
        peerConnection?.createAnswer(sdpObserver, mediaConstraint)
    }

    fun setLocalDescription(observer: AppSdpObserver, sdp: SessionDescription?) {
        peerConnection?.setLocalDescription(observer, sdp)
    }

    fun setRemoteDescription(observer: AppSdpObserver, sdp: SessionDescription?){
        peerConnection?.setRemoteDescription(observer, sdp)
    }

    fun addIceCandidate(iceCandidate: IceCandidate?){
        peerConnection?.addIceCandidate(iceCandidate)
    }

    fun startLocalSurface(context: Context, surfaceViewRenderer: SurfaceViewRenderer){

        val videoCapturer = getVideoCapturer(context)

        val surfaceTextureHelper =
            SurfaceTextureHelper.create(Thread.currentThread().name, eglBase.eglBaseContext)

        videoCapturer?.initialize(
            surfaceTextureHelper,
            surfaceViewRenderer.context, localVideoSource.capturerObserver
        )

        videoCapturer?.startCapture(320, 240, 30)

        val localVideoTrack = peerConnectionFactory.createVideoTrack("local_video", localVideoSource)
        localVideoTrack?.addSink(surfaceViewRenderer)

        val localAudioTrack = peerConnectionFactory.createAudioTrack("local_audio", localAudioSource)

        val localStream = peerConnectionFactory.createLocalMediaStream("local_stream").apply {
            addTrack(localVideoTrack)
            addTrack(localAudioTrack)
        }
        // 피어로 스트림 전송
        peerConnection?.addStream(localStream)
    }

    fun initSurfaceView(surfaceViewRenderer: SurfaceViewRenderer){
        surfaceViewRenderer.run {
            setEnableHardwareScaler(true)
            setMirror(true)
            init(eglBase.eglBaseContext, null)
        }
    }

    private fun getVideoCapturer(context: Context): CameraVideoCapturer? {
            return Camera2Enumerator(context).run {
                deviceNames.find { isFrontFacing(it) }?.let {
                    createCapturer(it, null)
                }
            }
    }
    
    fun getLocalDescription(): SessionDescription?{
        return peerConnection?.localDescription
    }

    fun getRemoteDescription(): SessionDescription? {
        return peerConnection?.remoteDescription
    }

    fun closeConnection(){
        peerConnection?.close()
    }

}