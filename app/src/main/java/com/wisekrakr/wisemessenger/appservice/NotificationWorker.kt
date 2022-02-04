package com.wisekrakr.wisemessenger.appservice

//class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
//
//    override fun doWork(): Result {
//
//        println("+++++++++++++++++++++++============================+++++++++++++++++++++++")
//
//        val data: Data = inputData
//
//        Log.d(TAG, "WE GOT SOME DATA: $data")
//
//        val notif = data.getStringArray("notifications")
//
//        if (notif.isNullOrEmpty()){
//            return Result.retry()
//        }
//
//        showNotification(
//            notif[1],
//            notif[0],
//        )
//
//        // Indicate whether the work finished successfully with the Result
//        return Result.success()
//    }
//
//
//    private fun getCustomDesign(
//        title: String?,
//        message: String?,
//    ): RemoteViews {
//
//        val remoteViews = RemoteViews(
//            applicationContext.packageName,
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
//    ) {
//        val intent = Intent(applicationContext, HomeActivity::class.java)
//        val channelId = "notification_channel"
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)// clears all activities and puts Home on top
//        val pendingIntent = PendingIntent.getActivity(
//            applicationContext, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT)
//
//        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
//            applicationContext,
//            channelId)
//            .setSmallIcon(R.drawable.logo_small)
//            .setAutoCancel(true)
//            .setVibrate(longArrayOf(1000, 1000, 1000,
//                1000, 1000))
//            .setOnlyAlertOnce(true)
//            .setContentIntent(pendingIntent)
//
//        builder = builder.setContent(getCustomDesign(title, message))
//
//        val notificationManager = applicationContext.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT
//            >= Build.VERSION_CODES.O
//        ) {
//            val notificationChannel = NotificationChannel(
//                channelId, applicationContext.packageName,
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
//}
