package com.theveloper.pixelplay.data.service.http

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MediaFileHttpServerService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START_SERVER = "com.theveloper.pixelplay.action.START_MEDIA_HTTP_SERVER"
        @Volatile var isServerRunning: Boolean = false
        @Volatile var isServerStarting: Boolean = false
        @Volatile var serverAddress: String? = null
        @Volatile var serverPrefixLength: Int? = null
        @Volatile var lastFailureReason: FailureReason? = null
        @Volatile var lastFailureMessage: String? = null
    }

    enum class FailureReason {
        NO_NETWORK_ADDRESS,
        FOREGROUND_START_EXCEPTION,
        START_EXCEPTION
    }
}
