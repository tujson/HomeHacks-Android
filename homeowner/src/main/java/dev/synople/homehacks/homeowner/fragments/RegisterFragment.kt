package dev.synople.homehacks.homeowner.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dev.synople.homehacks.common.models.Homeowner
import dev.synople.homehacks.homeowner.AppContext
import dev.synople.homehacks.homeowner.MainActivity

import dev.synople.homehacks.homeowner.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_register, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).bottomNavigationView.visibility = View.GONE

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val address = etAddress.text.toString()

            AppContext.user = Homeowner(FirebaseAuth.getInstance().currentUser!!.uid, name, address)

            FirebaseFirestore.getInstance().collection("homeowners").document(AppContext.user.id).set(AppContext.user)
                .addOnSuccessListener {
                    (activity as MainActivity).bottomNavigationView.visibility = View.VISIBLE
                    Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_auditFragment)
                }
        }
    }
}
