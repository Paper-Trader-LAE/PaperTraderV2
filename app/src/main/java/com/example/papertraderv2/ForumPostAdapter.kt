package com.example.papertraderv2.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.R
import com.example.papertraderv2.data.ForumRepository
import com.example.papertraderv2.models.ForumPost

class ForumPostAdapter(
    private val posts: List<ForumPost>,
    private val onClick: (ForumPost) -> Unit
) : RecyclerView.Adapter<ForumPostAdapter.PostViewHolder>() {

    private val repo = ForumRepository()

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.postTitle)
        val content: TextView = view.findViewById(R.id.postContent)
        val upvoteBtn: ImageView = view.findViewById(R.id.upvoteBtn)
        val downvoteBtn: ImageView = view.findViewById(R.id.downvoteBtn)
        val commentBtn: ImageView = view.findViewById(R.id.commentBtn)
        val saveBtn: ImageView = view.findViewById(R.id.saveBtn)
        val voteCount: TextView = view.findViewById(R.id.voteCount)
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
        holder.voteCount.text = (post.upvotes - post.downvotes).toString()

        holder.itemView.setOnClickListener {
            onClick(post)
        }

        holder.commentBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("postId", post.id)
                putString("title", post.title)
                putString("content", post.content)
                putString("author", post.author)
            }
            Navigation.findNavController(holder.itemView)
                .navigate(R.id.commentsFragment, bundle)
        }

        holder.upvoteBtn.setOnClickListener {
            repo.upvotePost(post.id)
        }

        holder.downvoteBtn.setOnClickListener {
            repo.downvotePost(post.id)
        }

        holder.saveBtn.setOnClickListener {
            // placeholder for save feature
        }
    }

    override fun getItemCount() = posts.size
}