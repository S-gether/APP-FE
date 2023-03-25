package com.sgether.webrtc

import android.content.Context
import com.sgether.R
import com.sgether.webrtc.observer.AppSdpObserver
import org.pytorch.*
import org.webrtc.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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

        


    }
    //val module: Module = Module.load(assetFilePath(this, "model.ptl"))
    fun assetFilePath(context: Context, asset: String): String {
        val file = File(context.filesDir, asset)

        try {
            val inpStream: InputStream = context.assets.open(asset)
            try {
                val outStream = FileOutputStream(file, false)
                val buffer = ByteArray(4 * 1024)
                var read: Int

                while (true) {
                    read = inpStream.read(buffer)
                    if (read == -1) {
                        break
                    }
                    outStream.write(buffer, 0, read)
                }
                outStream.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "오류발생"
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
                PeerConnection.IceServer.builder("stun:iphone-stun.strato-iphone.de:3478").createIceServer(),
                PeerConnection.IceServer("stun:openrelay.metered.ca:80"),
                PeerConnection.IceServer("turn:openrelay.metered.ca:80","openrelayproject","openrelayproject"),
                PeerConnection.IceServer("turn:openrelay.metered.ca:443","openrelayproject","openrelayproject"),
                PeerConnection.IceServer("turn:openrelay.metered.ca:443?transport=tcp","openrelayproject","openrelayproject"),
            ),
            observer
        )
    }

    fun createOffer(peerConnection: PeerConnection?, sdpObserver: AppSdpObserver) {
        val mediaConstraint = MediaConstraints().apply {
            mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo", "true"
                )
            )
        }
        peerConnection?.createOffer(sdpObserver, mediaConstraint)
    }

    fun createAnswer(peerConnection: PeerConnection?, sdpObserver: AppSdpObserver) {
        peerConnection?.createAnswer(sdpObserver, MediaConstraints())
    }

    fun setLocalDescription(
        peerConnection: PeerConnection?,
        observer: AppSdpObserver,
        sdp: SessionDescription?
    ) {
        peerConnection?.setLocalDescription(observer, sdp)
    }

    fun setRemoteDescription(
        peerConnection: PeerConnection?,
        observer: AppSdpObserver,
        sdp: SessionDescription?
    ) {
        peerConnection?.setRemoteDescription(observer, sdp)
    }

    fun addIceCandidate(peerConnection: PeerConnection?, iceCandidate: IceCandidate?) {
        peerConnection?.addIceCandidate(iceCandidate)
    }

    fun close(peerConnection: PeerConnection?) {
        peerConnection?.close()
    }


}