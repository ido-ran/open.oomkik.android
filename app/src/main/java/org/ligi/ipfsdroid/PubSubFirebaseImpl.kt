package org.ligi.ipfsdroid

import com.google.firebase.database.*


class PubSubFirebaseImpl(private val handler: (photoHash: String) -> Unit) {

    // Write a message to the database
    val database = FirebaseDatabase.getInstance();
    val myPeerRef = database.getReference("test/1-2-3");

    init {
        // Read from the database
        myPeerRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(snapshot: DataSnapshot?, previousChildName: String?) {
                var x = snapshot!!.key
                val photoEvent : PhotoEvent? = snapshot?.getValue(PhotoEvent::class.java)
                if (photoEvent != null) {
                    System.out.println(photoEvent.photoHash)
                    handler(photoEvent.photoHash)
                }
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}