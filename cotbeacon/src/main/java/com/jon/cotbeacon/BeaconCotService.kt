package com.jon.cotbeacon

import com.jon.common.cot.ChatCursorOnTarget
import com.jon.common.repositories.IDeviceUidRepository
import com.jon.common.service.CotService
import com.jon.cotbeacon.chat.ChatThreadManager
import com.jon.cotbeacon.chat.IChatRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BeaconCotService : CotService() {
    @Inject
    lateinit var chatRepository: IChatRepository

    @Inject
    lateinit var deviceUidRepository: IDeviceUidRepository

    private val chatThreadManager by lazy {
        ChatThreadManager(
                prefs,
                chatRepository,
                deviceUidRepository,
                socketRepository
        )
    }

    override fun start() {
        super.start()
        chatThreadManager.start()
    }

    override fun shutdown() {
        super.shutdown()
        chatThreadManager.shutdown()
    }

    fun sendChat(chatMessage: ChatCursorOnTarget) {
        chatThreadManager.sendChat(chatMessage)
    }
}