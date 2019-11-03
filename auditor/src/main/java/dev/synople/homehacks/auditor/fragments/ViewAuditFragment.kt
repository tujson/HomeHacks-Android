package dev.synople.homehacks.auditor.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dev.synople.homehacks.auditor.AppContext
import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.common.models.Audit
import kotlinx.android.synthetic.main.fragment_view_audit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import android.net.Uri
import android.provider.CalendarContract
import com.squareup.picasso.Picasso
import dev.synople.homehacks.common.auditTimeLength


class ViewAuditFragment : Fragment(), CoroutineScope {

    private val args: ViewAuditFragmentArgs by navArgs()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private lateinit var audit: Audit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_view_audit, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audit = args.audit

        tvHomeownerName.text = audit.homeownerName
        tvAddress.text = audit.address

        if (audit.responses.isNotEmpty() && audit.responses[0].response.isNotEmpty()) {
            btnStartAudit.text = "Upload Audit"
            btnStartAudit.background = ContextCompat.getDrawable(context!!, R.drawable.button_green)
        }

        FirebaseStorage.getInstance()
            .getReference("profilePictures")
            .child(audit.homeownerId)
            .downloadUrl
            .addOnSuccessListener {
                Picasso.get()
                    .load(it)
                    .into(ivHomeowner)
            }

        btnNavigate.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("geo:0,0?q=${audit.address}")
            val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }

        btnCalendar.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, audit.scheduledTime)
                .putExtra(
                    CalendarContract.EXTRA_EVENT_END_TIME,
                    audit.scheduledTime + (auditTimeLength * 60000)
                )
                .putExtra(
                    CalendarContract.Events.TITLE,
                    "[HomeHacks] Audit for ${audit.homeownerName}"
                )
                .putExtra(
                    CalendarContract.Events.DESCRIPTION,
                    "Auditing ${audit.homeownerName}'s home.\nFrom HomeHacks app."
                )
                .putExtra(CalendarContract.Events.EVENT_LOCATION, audit.address)
                .putExtra(
                    CalendarContract.Events.AVAILABILITY,
                    CalendarContract.Events.AVAILABILITY_BUSY
                )

            startActivity(intent)
        }

        btnStartAudit.setOnClickListener {
            if (audit.responses.isNotEmpty() && audit.responses[0].response.isNotEmpty()) {
                AppContext.user.scheduledAudits.clear()

                launch {
                    withContext(Dispatchers.IO) {
                        FirebaseFirestore.getInstance()
                            .collection("auditors")
                            .document(AppContext.user.id)
                            .set(AppContext.user)
                    }

                    withContext(Dispatchers.IO) {
                        FirebaseFirestore.getInstance()
                            .collection("pendingAudits")
                            .document(audit.homeownerId)
                            .delete()
                    }

                    withContext(Dispatchers.IO) {
                        FirebaseFirestore.getInstance()
                            .collection("audits")
                            .document(audit.homeownerId)
                            .collection("audits")
                            .document(audit.id)
                            .set(audit)
                    }

                    withContext(Dispatchers.IO) {
                        audit.responses.forEach { response ->

                            for (i in 0 until response.images.size) {
                                FirebaseStorage.getInstance()
                                    .getReference("${audit.id}/${response.id}/${response.images[i]}")
                                    .putFile(response.imageUris[i])
                            }
                        }
                    }

                    Navigation.findNavController(view)
                        .navigate(R.id.action_viewAuditFragment_to_calendarFragment)
                }
            } else {
                Navigation.findNavController(view)
                    .navigate(
                        ViewAuditFragmentDirections.actionViewAuditFragmentToSurveyFragment(
                            audit
                        )
                    )
            }
        }
    }
}
