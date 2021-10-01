package com.wisekrakr.wisemessenger.app.activity.chat

//class NewMessageActivity : BaseActivity<ActivityNewMessageBinding>() {
//
//    private lateinit var contactsAdapter: ContactsAdapter
//    private var arrayContacts = ArrayList<User>()
//    private var duplicateChatRoomUid: String? = ""
//
//    override val bindingInflater: (LayoutInflater) -> ActivityNewMessageBinding =
//        ActivityNewMessageBinding::inflate
//
//    override fun setup() {
//
//        contactsAdapter = ContactsAdapter()
//
//        onShowContacts()
//
//        contactsAdapter.setClickListener(onContactClick)
//    }
//
//    override fun supportBar() {
//        supportActionBar?.title = "Select Contact"
//    }
//
//    companion object {
//        const val CONTACT_KEY = "contact"
//        const val CHAT_ROOM_KEY = "chat_room"
//    }
//
//
//    private val onContactClick = object : ContactsAdapter.OnItemClickListener {
//        override fun onClick(contact: User) {
//            Log.d(ACTIVITY_TAG, "Clicked on: ${contact.username} ")
//
//            findChatRoomUid(contact)
//
//            launch {
//                if (duplicateChatRoomUid.isNullOrEmpty()) {
//                    getChatRoom(duplicateChatRoomUid!!).addListenerForSingleValueEvent(object :
//                        ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            val chatRoom = snapshot.getValue(ChatRoom::class.java)
//
//                            if (chatRoom != null) {
//                                startChatting(contact)
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            Log.e(ACTIVITY_TAG, error.message)
//                        }
//                    })
//                } else {
//                    onCreateNewChatRoom(contact)
//                }
//            }
//        }
//    }

//
//    private fun findChatRoomUid(contact: User) {
//        val uIds = HashSet<String>()
//        HomeActivity.currentUser?.participatingChatRooms?.forEach { myChatRoom ->
//
//            uIds.add(myChatRoom)
//        }
//        contact.participatingChatRooms.forEach { contactChatRoom ->
//
//            if (uIds.contains(contactChatRoom))
//                duplicateChatRoomUid = contactChatRoom
//        }
//
//        Log.e(ACTIVITY_TAG, "DUPLICATE CHAT ROOM = $duplicateChatRoomUid")
//
//    }
//
//    private fun onCreateNewChatRoom(contact: User) {
//        createChatRoom(
//            ChatRoom(
//                arrayListOf(
//                    firebaseAuth.currentUser?.uid.toString(),
//                    contact.uid
//                )
//            )
//        ).addOnSuccessListener {
//            startChatting(contact)
//            Log.d(ACTIVITY_TAG, "Created new chat room")
//
//        }.addOnFailureListener {
//            Log.d(ACTIVITY_TAG, "Failed to create new chat room ${it.cause}")
//        }
//    }
//
//
//    private fun startChatting(contact: User) {
//        val intent = Intent(this@NewMessageActivity, PrivateChatActivity::class.java)
//            .putExtra(CONTACT_KEY, contact)
//        startActivity(intent)
//        finish()
//    }
//
//    private fun onShowContacts() {
//        launch {
//            this.let {
//                getUsers().addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        snapshot.children.forEach {
////                            Log.d(ACTIVITY_TAG, it.toString())
//                            val user = it.getValue(User::class.java)!!
//
//                            if (user.uid != firebaseAuth.uid) {
//                                arrayContacts.add(it.getValue(User::class.java)!!)
//                            }
//                        }
//
//                        contactsAdapter.setData(arrayContacts)
//
//                        binding.tvNumberOfContactsNewMessage.text = arrayContacts.size.toString()
//
//                        binding.recyclerViewNewMessage.layoutManager = LinearLayoutManager(
//                            this@NewMessageActivity,
//                            LinearLayoutManager.VERTICAL,
//                            false
//                        )
//                        binding.recyclerViewNewMessage.setHasFixedSize(true)
//                        binding.recyclerViewNewMessage.adapter = contactsAdapter
//
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        Log.e(ACTIVITY_TAG, error.message)
//                    }
//                })
//
//                Log.d(ACTIVITY_TAG, "Showing contacts.... ")
//            }
//        }
//    }
//
//}