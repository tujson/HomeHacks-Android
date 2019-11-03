package dev.synople.homehacks.homeowner.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import dev.synople.homehacks.common.models.Audit
import dev.synople.homehacks.common.models.Question

import dev.synople.homehacks.homeowner.R
import dev.synople.homehacks.homeowner.adapters.ResponseAdapter
import kotlinx.android.synthetic.main.fragment_view_audit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import kotlin.coroutines.CoroutineContext

class ViewAuditFragment : Fragment(), CoroutineScope {
    private val args: ViewAuditFragmentArgs by navArgs()

    private lateinit var audit: Audit
    private var questions: MutableList<Question> = mutableListOf()
    private lateinit var adapter: ResponseAdapter

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_view_audit, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audit = args.audit

        val sdf = SimpleDateFormat("MMM dd, YYYY")

        tvDate.text = sdf.format(audit.performedTime)
        tvAuditorName.text = audit.auditorName
        tvSurveyVersion.text = audit.surveyVersion

        FirebaseStorage.getInstance()
            .getReference("profilePictures")
            .child(audit.auditorId)
            .downloadUrl
            .addOnSuccessListener {
                Picasso.get()
                    .load(it)
                    .into(ivAuditor)
            }

        // Download survey
        launch {
            val tempFile = File.createTempFile("questions", "json")
            FirebaseStorage.getInstance().getReference("surveys/${audit.surveyVersion}.json")
                .getFile(tempFile).await()

            val text = tempFile.readText()
            val listType = object : TypeToken<ArrayList<Question>>() {

            }.type

            questions.clear()
            questions.addAll(Gson().fromJson(text, listType) as List<Question>)

            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
                rvQuestions.visibility = View.VISIBLE
            }
        }

        adapter = ResponseAdapter(questions, audit.responses) {

        }
        rvQuestions.adapter = adapter
    }
}
