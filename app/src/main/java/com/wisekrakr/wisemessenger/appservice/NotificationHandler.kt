package com.wisekrakr.wisemessenger.appservice

//class NotificationHandler {
//
//    @SuppressLint("RestrictedApi")
//    fun onNotificationReceived(notification: Notification, context: Context) {
//
//        val stringArrayList: Array<String> = arrayOf(
//            notification.message,
//            notification.type.toString()
//        )
//
//        val data: Data = Data.Builder().putStringArray("notifications", stringArrayList).build()
//
//        val constraints: Constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//
////        val request: PeriodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(5, TimeUnit.SECONDS)
////            .setInputData(data)
////            .setConstraints(constraints)
////            .setInitialDelay(5, TimeUnit.SECONDS)
////            .addTag("NOTIFICATION")
////            .build()
////
////        WorkManager.getInstance(context).enqueue(
////            request
////        )
//
//
//    }
//
//
//
//}