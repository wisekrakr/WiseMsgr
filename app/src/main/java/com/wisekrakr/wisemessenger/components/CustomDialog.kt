package com.wisekrakr.wisemessenger.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.wisekrakr.wisemessenger.R

class CustomDialog(context: Context) : Dialog(context) {

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)
    }


}