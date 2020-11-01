package com.jon.cotbeacon.service.runnables

import android.content.SharedPreferences
import com.jon.common.cot.ChatCursorOnTarget
import com.jon.common.repositories.ISocketRepository
import com.jon.common.utils.DataFormat
import com.jon.cotbeacon.repositories.IChatRepository

abstract class ChatSendRunnable(
        prefs: SharedPreferences,
        socketRepository: ISocketRepository,
        chatRepository: IChatRepository,
        protected val chatMessage: ChatCursorOnTarget
) : ChatRunnable(prefs, socketRepository, chatRepository) {

    protected var dataFormat = DataFormat.fromPrefs(prefs)
}