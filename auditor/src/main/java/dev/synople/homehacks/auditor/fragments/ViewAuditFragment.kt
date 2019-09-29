package dev.synople.homehacks.auditor.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import dev.synople.homehacks.auditor.AppContext
import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.common.models.Audit
import kotlinx.android.synthetic.main.fragment_view_audit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

//        if (audit.responses[0].response.isNotEmpty()) {
//            btnStartAudit.text = "Upload Audit"
//            btnStartAudit.background = ContextCompat.getDrawable(context!!, R.drawable.button_green)
//        }

        btnStartAudit.setOnClickListener {
//            if (audit.responses[0].response.isNotEmpty()) {
//                AppContext.user.scheduledAudits.clear()
//
//                launch {
//                    withContext(Dispatchers.IO) {
//                        FirebaseFirestore.getInstance()
//                            .collection("auditors")
//                            .document(AppContext.user.id)
//                            .set(AppContext.user)
//                    }
//
//                    withContext(Dispatchers.IO) {
//                        FirebaseFirestore.getInstance()
//                            .collection("pendingAudits")
//                            .document(audit.homeownerId)
//                            .delete()
//                    }
//
//                    withContext(Dispatchers.IO) {
//                        FirebaseFirestore.getInstance()
//                            .collection("audits")
//                            .document(audit.homeownerId)
//                            .collection("audits")
//                            .document(audit.id)
//                            .set(audit)
//                    }
//
//                    Navigation.findNavController(view)
//                        .navigate(R.id.action_viewAuditFragment_to_calendarFragment)
//                }
//            } else {
                Navigation.findNavController(view)
                    .navigate(
                        ViewAuditFragmentDirections.actionViewAuditFragmentToSurveyFragment(
                            audit
                        )
                    )
//            }
        }
    }
}
