package com.wisekrakr.wisemessenger.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment


object Extensions {
    val Any.TAG: String
        get() {
            val tag = javaClass.simpleName
            return if (tag.length <= 23) tag else tag.substring(0, 23)
        }

    val Activity.ACTIVITY_TAG: String
        get() {
            val tag = javaClass.simpleName
            return if (tag.length <= 23) tag else tag.substring(0, 23)
        }

    val Fragment.FRAGMENT_TAG: String
        get() {
            val tag = javaClass.simpleName
            return if (tag.length <= 23) tag else tag.substring(0, 23)
        }

    fun Activity.makeToast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Fragment.makeToast(msg: String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    fun notification(
        context: Context,
        title: String?,
        message: String?
    ) {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, com.wisekrakr.wisemessenger.R.style.AlertDialog)

        builder
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton("CANCEL") { dialog, which ->
                dialog.cancel()
            }
        return builder.create().show()
    }

    fun isNotEmpty(arrayOfInputs: Array<EditText>): Boolean {
        val inputsArray = ArrayList<EditText>()

        arrayOfInputs.forEach {
            if(it.text.toString().trim().isNotEmpty()){
                Log.d(TAG, it.text.toString())
                inputsArray.add(it)
            }
        }

        return inputsArray.size == arrayOfInputs.size
    }

    fun isRequired(arrayOfInputs: Array<EditText>){
        arrayOfInputs.forEach { input ->
            if (input.text.toString().trim().isEmpty()) {
                input.error = "${input.hint} is required"
            }
        }
    }
}