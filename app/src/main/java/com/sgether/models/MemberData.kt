package com.sgether.models

import org.webrtc.PeerConnection
import org.webrtc.VideoTrack

data class MemberData(
    var name: String,
    var socketId: String,
    var peerConnection: PeerConnection? = null,
    var isLocal: Boolean = false,
)
