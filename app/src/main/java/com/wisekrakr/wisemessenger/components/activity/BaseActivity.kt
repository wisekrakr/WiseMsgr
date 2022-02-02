package com.wisekrakr.wisemessenger.components.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository
import com.wisekrakr.wisemessenger.components.activity.actions.SearchActivity
import com.wisekrakr.wisemessenger.components.activity.chat.ContactsActivity
import com.wisekrakr.wisemessenger.components.activity.profile.ProfileSettingsActivity
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.utils.Actions.IntentActions.returnToActivityWithFlags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), CoroutineScope{

    private lateinit var viewBinding: VB
    abstract val bindingInflater: (LayoutInflater) -> VB
    var isInBackground: Boolean = false

    protected val binding: VB get() = viewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = bindingInflater.invoke(layoutInflater)
        setContentView(viewBinding.root)
        setup()
        supportBar()

        if(FirebaseUtils.firebaseAuth.currentUser?.uid != null){
            if(isInBackground)
                UserProfileRepository.updateUserConnectivityStatus(
                    FirebaseUtils.firebaseAuth.currentUser?.uid.toString(),
                    "Offline"
                )
            else
                UserProfileRepository.updateUserConnectivityStatus(
                    FirebaseUtils.firebaseAuth.currentUser?.uid.toString(),
                    "Online"
                )
        }
    }


    abstract fun setup()

    abstract fun supportBar()

    private val job = Job()

    override val coroutineContext = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
//        viewBinding = null

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_nav, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
//                finish()
            }
            R.id.nav_find_contacts -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }
            R.id.nav_contacts -> {
                startActivity(Intent(this, ContactsActivity::class.java))
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, ProfileSettingsActivity::class.java))
            }
            R.id.nav_sign_out -> {
                UserProfileRepository.updateUserConnectivityStatus(
                    FirebaseUtils.firebaseAuth.currentUser?.uid.toString(),
                    "Offline"
                )

                FirebaseUtils.firebaseAuth.signOut()

                startActivity(Intent(this, StartActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        returnToActivityWithFlags(HomeActivity::class.simpleName.toString())
    }
}