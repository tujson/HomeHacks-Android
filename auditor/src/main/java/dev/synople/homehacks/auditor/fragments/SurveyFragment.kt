package dev.synople.homehacks.auditor.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs

import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.common.models.Audit
import kotlinx.android.synthetic.main.fragment_survey.*

class SurveyFragment : Fragment() {

    private val args: SurveyFragmentArgs by navArgs()
    private lateinit var audit: Audit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_survey, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audit = args.audit

        tvHeader.text = "< Auditing - ${audit.homeownerName}'s Home"
        tvHeader.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(SurveyFragmentDirections.actionSurveyFragmentToViewAuditFragment(audit))
        }

        tvArea.text = audit.questions[0].area
        tvQuestion.text = audit.questions[0].question

        btnYes.setOnClickListener {
            audit.questions[0].answer = "true"
            audit.questions[0].comments = etComments.text.toString()

            Navigation.findNavController(view)
                .navigate(SurveyFragmentDirections.actionSurveyFragmentToViewAuditFragment(audit))
        }
    }
}
