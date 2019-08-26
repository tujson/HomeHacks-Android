package dev.synople.homehacks.auditor.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import dev.synople.homehacks.auditor.AppContext

import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.common.models.Audit
import kotlinx.android.synthetic.main.fragment_view_audit.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

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

        if (audit.questions[0].answer.isNotEmpty()) {
            btnStartAudit.text = "Upload Audit"
            btnStartAudit.background = ContextCompat.getDrawable(context!!, R.drawable.button_green)
        }

        btnStartAudit.setOnClickListener {
            if (audit.questions[0].answer.isNotEmpty()) {
                AppContext.user.scheduledAudits.clear()

                launch {
                    // TODO: Can we parallelize this?
                    FirebaseFirestore.getInstance()
                        .collection("auditors")
                        .document(AppContext.user.id)
                        .set(AppContext.user)
                        .await()

                    FirebaseFirestore.getInstance()
                        .collection("pendingAudits")
                        .document(audit.homeownerId)
                        .delete()
                        .await()

                    FirebaseFirestore.getInstance()
                        .collection("audits")
                        .document(audit.homeownerId)
                        .collection("audits")
                        .document(audit.id)
                        .set(audit)
                        .await()

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
