package dev.synople.homehacks.homeowner.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import dev.synople.homehacks.homeowner.AppContext

import dev.synople.homehacks.homeowner.R
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_profile, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etName.setText(AppContext.user.name)
        etAddress.setText(AppContext.user.address)

        // TODO: Add PlacePicker instead of etAddress
        // TODO: Maybe add map view?

        btnSave.setOnClickListener {
            AppContext.user.name = etName.text.toString()
            AppContext.user.address = etAddress.text.toString()

            FirebaseFirestore.getInstance().collection("homeowners")
                .document(AppContext.user.id)
                .set(AppContext.user)
                .addOnSuccessListener {
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
