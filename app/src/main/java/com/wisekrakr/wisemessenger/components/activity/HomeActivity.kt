package com.wisekrakr.wisemessenger.components.activity

import android.content.Intent
import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseUser
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.api.adapter.TabsAccessorAdapter
import com.wisekrakr.wisemessenger.api.model.User
import com.wisekrakr.wisemessenger.appservice.tasks.ApiManager
import com.wisekrakr.wisemessenger.components.activity.profile.ProfileSettingsActivity
import com.wisekrakr.wisemessenger.databinding.ActivityHomeBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.updateFirebaseUser
import com.wisekrakr.wisemessenger.utils.Actions.IntentActions.returnToActivityWithFlags
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import kotlinx.coroutines.launch
import java.util.*

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private lateinit var tabsAccessorAdapter: TabsAccessorAdapter

    companion object {
        var currentUser: User? = null
    }

    override val bindingInflater: (LayoutInflater) -> ActivityHomeBinding =
        ActivityHomeBinding::inflate

    override fun setup() {
        val user: FirebaseUser? = firebaseAuth.currentUser
        if (user?.uid == null) {
            returnToActivityWithFlags(StartActivity::class.simpleName.toString())
        } else {
            getCurrentUser(user)
        }

    }

    override fun supportBar() {
        onCreateTabLayout()
    }

    private fun getCurrentUser(user: FirebaseUser) {
        launch {

            ApiManager.CurrentUser.onGetUser(
                user.uid
            ) {
                currentUser = it

                updateFirebaseUser(currentUser!!.username)
                makeToast("Welcome back ${currentUser!!.username}")

                if (currentUser!!.profileUid.isBlank()) {
                    val username = intent.getStringExtra("username")

                    val i = Intent(this@HomeActivity,
                        ProfileSettingsActivity::class.java)

                    i.putExtra("username", username)

                    startActivity(i)
                    finish()
                }

                binding.tvNameHome.text = currentUser!!.username
            }


        }
    }


    private fun onCreateTabLayout() {

        tabsAccessorAdapter = TabsAccessorAdapter(supportFragmentManager, lifecycle)

        val viewPager: ViewPager2 = binding.viewpagerHome
        viewPager.adapter = tabsAccessorAdapter
        viewPager.isUserInputEnabled = true //enable swipe

        val tabLayout: TabLayout = binding.tabsHome
        tabLayout.tabMode = TabLayout.MODE_FIXED  // show all tabs on screen
        tabLayout.isInlineLabel = true // set tab icons next to the text, not above

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->

            when (position) {
                0 -> {
                    tab
                        .setText("Chat")
                        .setIcon(R.drawable.icon_chat)
                }
                1 -> {
                    tab
                        .setText("Groups")
                        .setIcon(R.drawable.icon_group_chat)
                }
                2 -> {
                    tab
                        .setText("Requests")
                        .setIcon(R.drawable.icon_request)
                }
            }

        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()

        ApiManager.Profiles.onUpdateUserConnectivityStatus("Offline")
    }
}