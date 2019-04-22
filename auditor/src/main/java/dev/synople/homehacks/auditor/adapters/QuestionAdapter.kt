package dev.synople.homehacks.auditor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.common.models.Question
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.screen_question.view.*

class QuestionAdapter(private val questions: MutableList<Question>) :
    RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {
    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindQuestion(question: Question) {
            containerView.tvArea.text = question.area
            containerView.tvQuestion.text = question.question

            containerView.btnYes.setOnClickListener {
                question.answer = "true"
            }

            containerView.btnNo.setOnClickListener {
                question.answer = "false"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.screen_question, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindQuestion(questions[position])
    }

    override fun getItemCount() = questions.size
}