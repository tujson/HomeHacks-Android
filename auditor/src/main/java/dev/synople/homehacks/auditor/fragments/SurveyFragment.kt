package dev.synople.homehacks.auditor.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.storage.FirebaseStorage

import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.common.models.Audit
import kotlinx.android.synthetic.main.fragment_survey.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.coroutines.CoroutineContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.synople.homehacks.auditor.adapters.QuestionAdapter
import dev.synople.homehacks.common.models.Question
import kotlinx.coroutines.withContext


class SurveyFragment : Fragment(), CoroutineScope {

    private val args: SurveyFragmentArgs by navArgs()
    private lateinit var audit: Audit
    private lateinit var questions: MutableList<Question>

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_survey, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audit = args.audit

        loadQuestions()

        ivLeft.setOnClickListener {
            vpQuestions.currentItem = vpQuestions.currentItem - 1
        }
        ivRight.setOnClickListener {
            vpQuestions.currentItem = vpQuestions.currentItem + 1
        }

        vpQuestions.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                tvProgress.text = "${position + 1}/${questions.size}"
                progressBar.progress = position
            }
        })
    }

    private fun loadQuestions() {
        launch {
            val tempFile = File.createTempFile("questions", "json")
            Log.v("SurveyFragment", "Location: ${audit.surveyVersion}")
            FirebaseStorage.getInstance().getReference("surveys/${audit.surveyVersion}.json")
                .getFile(tempFile).await()

            val text = tempFile.readText()
            val listType = object : TypeToken<ArrayList<Question>>() {

            }.type
            questions = Gson().fromJson(text, listType) as MutableList<Question>

            withContext(Dispatchers.Main) {
                vpQuestions.adapter = QuestionAdapter(questions)
                progressBar.max = questions.size
            }
        }
    }
}
