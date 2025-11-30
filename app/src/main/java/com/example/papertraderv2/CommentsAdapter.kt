package com.example.papertraderv2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.R
import com.example.papertraderv2.models.ForumComment

class CommentAdapter(
    private val comments: List<ForumComment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val author: TextView = view.findViewById(R.id.commentAuthor)
        val content: TextView = view.findViewById(R.id.commentContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.author.text = comment.author
        holder.content.text = comment.content
    }

    override fun getItemCount(): Int = comments.size
}