package com.sgether.models

import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack

data class MemberData(
    var type: String,
    var name: String,
    var socketId: String,
    var sdp: SessionDescription? = null,
    var peerConnection: PeerConnection? = null,
    var mediaStream: MediaStream? = null,
    var isLocal: Boolean = false,
)
