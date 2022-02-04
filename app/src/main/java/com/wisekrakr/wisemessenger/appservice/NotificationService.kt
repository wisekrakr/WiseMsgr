package com.wisekrakr.wisemessenger.appservice

//class NotificationService : Service() {
//    override fun onBind(intent: Intent): IBinder? {
//        return null
//    }
//
//
//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        Log.d(TAG, "SERVICE IS ACTIVATED")
//        val notificationHandler = NotificationHandler()
//
//
////        getNotifications(notificationHandler)
//
//
////        startForeground()
//
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    private fun getNotifications(notificationHandler: NotificationHandler) {
//        runBlocking {
//            launch {
//                NotificationRepository.getNotificationsForCurrentUser(
//                    FirebaseUtils.firebaseAuth.currentUser!!.uid
//                ).addChildEventListener(object : ChildEventListener {
//                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                        val notification = snapshot.getValue(Notification::class.java)
//
//                        if (notification != null) {
//                            Log.d(TAG, "SERVICE GOT NOTIFICATION $notification")
//
//                            notificationHandler.onNotificationReceived(notification,
//                                applicationContext)
//                        }
//                    }
//
//                    override fun onChildChanged(
//                        snapshot: DataSnapshot,
//                        previousChildName: String?,
//                    ) {
//                    }
//
//                    override fun onChildRemoved(snapshot: DataSnapshot) {}
//                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
//                    override fun onCancelled(error: DatabaseError) {}
//                })
//            }
//        }
//    }
//
//
//    private fun startForeground() {
//        val notificationIntent = Intent(this, HomeActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0,
//            notificationIntent, 0)
//
//        startForeground(NOTIFICATION_ID, NotificationCompat.Builder(this,
//            NOTIFICATION_CHANNEL_ID) // don't forget create a notification channel first
//            .setOngoing(true)
//            .setSmallIcon(R.drawable.icon_chat)
//            .setContentTitle(getString(R.string.app_name))
//            .setContentText("Service is running in background")
//            .setContentIntent(pendingIntent)
//            .build())
//    }
//
//    companion object {
//        private const val NOTIFICATION_ID = 1
//        private const val NOTIFICATION_CHANNEL_ID = "Channel_Id"
//    }
//
//    fun onNotificationReceived(notification: Notification, context: Context) {
//        when (notification.type) {
//            NotificationType.CHAT_REQUEST -> {
//                showNotification(
//                    "Chat Request",
//                    notification.fromUserName + " wants to chat with you",
//                    context
//                )
//            }
//            NotificationType.MESSAGE -> {
//                showNotification(
//                    "Chat Message",
//                    "You got a new message from " + notification.fromUserName,
//                    context
//                )
//            }
//        }
//
//    }
//
//    private fun getCustomDesign(
//        title: String?,
//        message: String?,
//        context: Context?,
//    ): RemoteViews {
//
//        val remoteViews = RemoteViews(
//            context!!.applicationContext.packageName,
//            R.layout.push_notification)
//        remoteViews.setTextViewText(R.id.title, title)
//        remoteViews.setTextViewText(R.id.message, message)
//        remoteViews.setImageViewResource(R.id.notification_icon,
//            R.drawable.logo_small)
//        return remoteViews
//    }
//
//    private fun showNotification(
//        title: String?,
//        message: String?,
//        context: Context?,
//    ) {
//        val intent = Intent(context, HomeActivity::class.java)
//        val channelId = "notification_channel"
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)// clears all activities and puts Home on top
//        val pendingIntent = PendingIntent.getActivity(
//            context, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT)
//
//        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
//            context!!.applicationContext,
//            channelId)
//            .setSmallIcon(R.drawable.logo_small)
//            .setAutoCancel(true)
//            .setVibrate(longArrayOf(1000, 1000, 1000,
//                1000, 1000))
//            .setOnlyAlertOnce(true)
//            .setContentIntent(pendingIntent)
//
//        builder = builder.setContent(getCustomDesign(title, message, context))
//
//        val notificationManager =
//            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT
//            >= Build.VERSION_CODES.O
//        ) {
//            val notificationChannel = NotificationChannel(
//                channelId, context.applicationContext.packageName,
//                NotificationManager.IMPORTANCE_DEFAULT)
//                .apply {
//                    lightColor = Color.RED
//                    enableLights(true)
//                }
//
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//        notificationManager.notify(0, builder.build())
//    }
//
//
//}