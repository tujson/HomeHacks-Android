package dev.synople.homehacks.homeowner.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.synople.homehacks.common.models.Homeowner
import dev.synople.homehacks.homeowner.AppContext
import dev.synople.homehacks.homeowner.R

const val RC_SIGN_IN = 1

class LoginFragment : Fragment() {

    private val TAG = "Homeowner LoginFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_login, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseAuth.getInstance().currentUser?.let {
            retrieveUser(it.uid)
        } ?: run {
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setLogo(R.drawable.wordlogo)
                    .setTheme(R.style.FirebaseTheme)
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        }
    }

    private fun retrieveUser(id: String) {
        FirebaseFirestore.getInstance()
            .collection("homeowners")
            .document(id)
            .get()
            .addOnSuccessListener {
                it.toObject(Homeowner::class.java)?.let { homeowner ->
                    AppContext.user = homeowner
                    Navigation.findNavController(view!!)
                        .navigate(R.id.action_loginFragment_to_auditFragment)
                } ?: run {
                    Navigation.findNavController(view!!)
                        .navigate(R.id.action_loginFragment_to_registerFragment)
                }
            }.addOnFailureListener {
                Log.e(TAG, "Retrieve Homeowner from database", it)
                Toast.makeText(context, "Error retrieving Homeowner", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                FirebaseAuth.getInstance().currentUser?.let {
                    retrieveUser(it.uid)
                }
            } else {
            }
        }
    }

}
