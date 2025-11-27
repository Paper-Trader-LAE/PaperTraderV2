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

    // ----- ViewHolder -----
    class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail: ImageView = itemView.findViewById(R.id.moduleThumbnail)
        val title: TextView = itemView.findViewById(R.id.moduleTitle)
        val description: TextView = itemView.findViewById(R.id.moduleDescription)
        val progress: ProgressBar = itemView.findViewById(R.id.moduleProgress)
    }

    // ----- Create ViewHolder -----
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_module, parent, false)
        return ModuleViewHolder(view)
    }

    // ----- Bind Data -----
    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = modules[position]

        holder.thumbnail.setImageResource(module.thumbnailRes)
        holder.title.text = module.title
        holder.description.text = module.description
        holder.progress.progress = module.progress

        holder.itemView.setOnClickListener {
            onClick(module)
        }
    }

    // ----- Item Count -----
    override fun getItemCount(): Int = modules.size
}