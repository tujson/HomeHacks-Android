package dev.synople.homehacks.auditor.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.common.models.Question
import dev.synople.homehacks.common.models.Response
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.screen_question.view.*

class QuestionAdapter(
    private val questions: List<Question>,
    private val responses: MutableList<Response>,
    private val onNext: () -> Unit,
    private val takePicture: () -> Unit
) :
    RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindQuestion(
            question: Question,
            response: Response,
            onNext: () -> Unit,
            takePicture: () -> Unit
        ) {
            containerView.tvLocation.text = question.location.capitalize()
            containerView.tvQuestion.text = question.question

            containerView.btnYes.setOnClickListener {
                containerView.btnYes.tag = "true"
                response.response = "yes"
                response.comments = containerView.etComments.text.toString()
                onNext()
            }

            containerView.btnNo.setOnClickListener {
                containerView.btnYes.tag = "false"
                response.response = "no"
                response.comments = containerView.etComments.text.toString()
                onNext()
            }

            containerView.etComments.setText(response.comments)

            containerView.tag = question.id

            containerView.btnAddImage.setOnClickListener {
                takePicture()
            }

            response.id = question.id
            if (response.imageUris.isNotEmpty()) {
                containerView.rvImages.layoutManager = LinearLayoutManager(
                    containerView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )

                containerView.rvImages.adapter = SurveyImageAdapter(response.imageUris) {}
                containerView.rvImages.setHasFixedSize(true)
                containerView.rvImages.isNestedScrollingEnabled = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.screen_question, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindQuestion(questions[position], responses[position], onNext, takePicture)
    }

    override fun getItemCount() = questions.size
}