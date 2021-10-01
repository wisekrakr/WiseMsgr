package com.wisekrakr.wisemessenger.app.activity

import android.util.Log
import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.adapter.TabsAccessorAdapter
import com.wisekrakr.wisemessenger.databinding.ActivityHomeBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.updateFirebaseUser
import com.wisekrakr.wisemessenger.model.User
import com.wisekrakr.wisemessenger.repository.UserRepository.getCurrentUser
import com.wisekrakr.wisemessenger.utils.Actions.IntentActions.returnToActivityWithFlags
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import kotlinx.coroutines.launch

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private lateinit var tabsAccessorAdapter: TabsAccessorAdapter
    private val contacts: ArrayList<User> = arrayListOf()

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
            this.let {
                getCurrentUser(user.uid).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        currentUser = snapshot.getValue(User::class.java)

                        if (currentUser != null) {
                            updateFirebaseUser(currentUser!!.username)
                            makeToast("Welcome Back ${currentUser!!.username}")

                            binding.tvNameHome.text = currentUser!!.username
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(ACTIVITY_TAG, error.message)
                    }
                })
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
                        .setText("Contacts")
                        .setIcon(R.drawable.icon_contacts)
                }
                1 -> {
                    tab
                        .setText("Chat")
                        .setIcon(R.drawable.icon_private_chat)
                }
                2 -> {
                    tab
                        .setText("Groups")
                        .setIcon(R.drawable.icon_group_chat)
                }
            }

        }.attach()
    }
}