package com.example.wallethub.Controllers

import android.util.Log
import com.example.wallethub.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserController {

    /**
     * Get user by email (case-insensitive & trimmed)
     * Ensures Firebase authentication before querying
     */
    fun getUserByEmail(email: String?, onResult: (user: UserModel?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("Firebase", "User not signed in. Cannot fetch users.")
            onResult(null)
            return
        }

        if (email.isNullOrBlank()) {
            onResult(null)
            return
        }

        val queryEmail = email.trim().lowercase()
        val database = FirebaseDatabase.getInstance().getReference("Users")

        // Read all users (rules allow authenticated users)
        database.orderByChild("email")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var foundUser: UserModel? = null

                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(UserModel::class.java)
                        Log.d("Firebase", "User fetched: $user")  // debug log
                        if (user?.email?.trim()?.lowercase() == queryEmail) {
                            foundUser = user
                            break
                        }
                    }

                    if (foundUser == null) {
                        Log.d("Firebase", "No user found with email: $queryEmail")
                    }
                    onResult(foundUser)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching user: ${error.message}")
                    onResult(null)
                }
            })
    }
}
