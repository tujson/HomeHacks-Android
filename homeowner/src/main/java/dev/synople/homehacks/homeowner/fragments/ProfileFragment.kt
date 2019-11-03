package dev.synople.homehacks.homeowner.fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import dev.synople.homehacks.homeowner.AppContext

import dev.synople.homehacks.homeowner.R
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class ProfileFragment : Fragment() {

    val PICK_IMAGE = 1

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

        FirebaseStorage.getInstance().getReference("profilePictures")
            .child(AppContext.user.id)
            .downloadUrl
            .addOnSuccessListener {
                Picasso.get()
                    .load(it)
                    .into(ivProfile)
            }.addOnFailureListener {
                Log.e("ProfileFragment", "FirebaseStorage loading profile picture", it)
            }

        ivProfile.setOnClickListener {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select profile picture")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)
        }

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

        btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Navigation.findNavController(it)
                .navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val inputStream = context!!.contentResolver.openInputStream(data.data!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val aspectRatio = bitmap.width / bitmap.height.toFloat()
            val height = 200

            val resizedBitmap = Bitmap.createScaledBitmap(
                bitmap,
                (height * aspectRatio).roundToInt(), height,
                false
            )
            val baos = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            FirebaseStorage.getInstance().getReference("profilePictures")
                .child(AppContext.user.id)
                .putBytes(baos.toByteArray())
                .addOnSuccessListener {
                    ivProfile.setImageBitmap(resizedBitmap)
                }
        }
    }
}
