package com.sgether.models

import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.VideoTrack

data class MemberData(
    var name: String,
    var socketId: String,
    var peerConnection: PeerConnection? = null,
    var mediaStream: MediaStream? = null,
    var isLocal: Boolean = false,
)
