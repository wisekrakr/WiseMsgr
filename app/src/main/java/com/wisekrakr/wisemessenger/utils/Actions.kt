package com.wisekrakr.wisemessenger.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.wisekrakr.wisemessenger.activity.HomeActivity
import com.wisekrakr.wisemessenger.activity.auth.RegisterActivity
import com.wisekrakr.wisemessenger.activity.StartActivity

object Actions {

    object ClassActions{
        /**
         *  Starts a new Activity with flags on the intent: FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK
         *  If back button on device get pushed, desktop of the device is shown and not the last activity
         */
        fun Activity.returnToActivityWithFlags(activityTag: String) {
            var intent: Intent? = null
            Log.d("Actions", "Returning to Activity: $activityTag")
            when(activityTag){
                "HomeActivity" ->{
                    intent = Intent(this, HomeActivity::class.java)
                }
                "StartActivity" ->{
                    intent = Intent(this, StartActivity::class.java)
                }
                "RegisterActivity" ->{
                    intent = Intent(this, RegisterActivity::class.java)
                }
            }
            Log.d("Actions", "Starting Activity: ${intent?.javaClass}")

            intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    object ImageActions{
        fun loadImage(imageUrl: String?, target: ImageView){
            Picasso.get().load(imageUrl).into(target)
        }
    }
}