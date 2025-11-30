package com.example.papertraderv2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.R
import com.example.papertraderv2.models.ForumPost

class ForumPostAdapter(
    private val posts: List<ForumPost>,
    private val onClick: (ForumPost) -> Unit
) : RecyclerView.Adapter<ForumPostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.postTitle)
        val content: TextView = view.findViewById(R.id.postContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.title.text = post.title
        holder.content.text = post.content

        holder.itemView.setOnClickListener { onClick(post) }
    }

    override fun getItemCount() = posts.size
}