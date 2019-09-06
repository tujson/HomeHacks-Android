package dev.synople.homehacks.auditor.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.synople.homehacks.auditor.AppContext

import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.common.models.Auditor

const val RC_SIGN_IN = 1

class LoginFragment : Fragment() {

    private val TAG = "Auditor LoginFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_login, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseAuth.getInstance().currentUser?.let {
            retrieveAuditor(it.uid)
        } ?: run {
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setLogo(R.drawable.wordlogo)
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        }
    }

    private fun retrieveAuditor(id: String) {
        FirebaseFirestore.getInstance().collection("auditors").document(id).get().addOnSuccessListener {
            it.toObject(Auditor::class.java)?.let { auditor ->
                AppContext.user = auditor
                Navigation.findNavController(view!!).navigate(R.id.action_loginFragment_to_calendarFragment)
            } ?: run {
                Toast.makeText(context, "Auditor not found. Please contact an admin to register.", Toast.LENGTH_LONG)
                    .show()
            }
        }.addOnFailureListener {
            Log.e(TAG, "Retrieve Auditor from database", it)
            Toast.makeText(context, "Error retrieving Auditor", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                FirebaseAuth.getInstance().currentUser?.let {
                    retrieveAuditor(it.uid)
                }
            } else {
            }
        }
    }

}
