package com.example.papertraderv2.data

import com.example.papertraderv2.models.ForumPost
import com.example.papertraderv2.models.ForumComment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ForumRepository {

    private val db = FirebaseFirestore.getInstance()
    private val postsRef = db.collection("forum_posts")

    // ---------------- POSTS ----------------

    suspend fun createPost(post: ForumPost) {
        // If id is empty, let Firestore create it
        if (post.id.isBlank()) {
            postsRef.add(post).await()
        } else {
            postsRef.document(post.id).set(post).await()
        }
    }

    suspend fun getPosts(): List<ForumPost> {
        val snapshot = postsRef
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.toObjects(ForumPost::class.java)
    }

    // ---------------- COMMENTS ----------------

    suspend fun createComment(postId: String, comment: ForumComment) {
        // Let Firestore auto-assign ID
        postsRef
            .document(postId)
            .collection("comments")
            .add(comment)
            .await()
    }

    suspend fun getComments(postId: String): List<ForumComment> {
        val snapshot = postsRef
            .document(postId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .await()

        return snapshot.toObjects(ForumComment::class.java)
    }
}