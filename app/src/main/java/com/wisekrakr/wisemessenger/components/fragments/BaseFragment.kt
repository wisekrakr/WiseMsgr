package com.wisekrakr.wisemessenger.components.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewbinding.ViewBinding
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.utils.Extensions.FRAGMENT_TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

typealias BindingInflater<VB> = (inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean) -> VB

abstract class BaseFragment<VB : ViewBinding> : Fragment(), CoroutineScope {

    // This property must be nullable to prevent view leaks
    // Never forget that fragments outlive their views !
    private var mutableViewBinding : VB? = null

    // This property gives us a not nullable view binding for simpler use
    protected val viewBinding: VB get() = mutableViewBinding!!

    // This property is abstract because it has to be declared by the child class
    protected abstract val bindingInflater: BindingInflater<VB>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mutableViewBinding = bindingInflater.invoke(inflater, container, false)

        UserProfileRepository.updateUserConnectivityStatus(
            FirebaseUtils.firebaseAuth.currentUser?.uid.toString(),
            "Online"
        )

        return viewBinding.root
    }

    fun refreshFragment(){
        Log.d(this.FRAGMENT_TAG, "Refreshing Fragment...")
        val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.detach(this)
        fragmentTransaction.attach(this)
        fragmentTransaction.commit()
    }

    private val job = Job()

    override val coroutineContext = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
//        viewBinding = null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Prevent view leaks
        mutableViewBinding = null
    }


}