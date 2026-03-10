package com.example.papertraderv2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.R
import com.example.papertraderv2.models.Lesson

class LessonAdapter(
    private val lessons: List<Lesson>,
    private val completedKeys: Set<String>,
    private val onClick: (Lesson) -> Unit
) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    class LessonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.lessonTitle)
        val summary: TextView = view.findViewById(R.id.lessonSummary)
        val status: ImageView = view.findViewById(R.id.lessonStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]
        holder.title.text = lesson.title
        holder.summary.text = lesson.summary

        val completed = completedKeys.contains(lesson.key)
        holder.status.setImageResource(
            if (completed) R.drawable.ic_check_circle else R.drawable.ic_lesson_circle
        )

        holder.itemView.setOnClickListener { onClick(lesson) }
    }

    override fun getItemCount(): Int = lessons.size
}