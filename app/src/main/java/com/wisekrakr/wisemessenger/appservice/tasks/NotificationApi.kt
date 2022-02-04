package com.wisekrakr.wisemessenger.appservice.tasks

import com.wisekrakr.wisemessenger.api.model.nondata.NotificationType

interface NotificationApi {
    fun onPushNotification(
        userProfileUid: String,
        userProfileUsername: String,
        currentUserUid: String,
        currentUsername: String,
        message: String,
        notificationType: NotificationType,
    )
}