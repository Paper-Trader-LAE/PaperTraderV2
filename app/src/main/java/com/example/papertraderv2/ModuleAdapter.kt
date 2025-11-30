package com.example.papertraderv2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.papertraderv2.R
import com.example.papertraderv2.models.Module

class ModuleAdapter(
    private val modules: List<Module>,
    private val onClick: (Module) -> Unit
) : RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>() {

    class ModuleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.moduleThumbnail)
        val title: TextView = view.findViewById(R.id.moduleTitle)
        val description: TextView = view.findViewById(R.id.moduleDescription)
        val progressBar: ProgressBar = view.findViewById(R.id.moduleProgress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_module, parent, false)
        return ModuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = modules[position]

        holder.thumbnail.setImageResource(module.thumbnailRes)
        holder.title.text = module.title
        holder.description.text = module.description
        holder.progressBar.progress = module.progress

        holder.itemView.setOnClickListener { onClick(module) }
    }

    override fun getItemCount() = modules.size
}