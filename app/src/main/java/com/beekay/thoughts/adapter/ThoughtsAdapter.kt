package com.beekay.thoughts.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beekay.thoughts.R
import com.beekay.thoughts.databinding.ItemThoughtBinding
import com.beekay.thoughts.model.Thought
import com.bumptech.glide.Glide
import java.io.File

/**
 * Created by Krishna by 16-11-2020
 */
class ThoughtsAdapter(private val context: Context,
                      private val clickListener: ClickListener<Thought>):
        RecyclerView.Adapter<ThoughtsAdapter.ThoughtViewHolder>() {

    private val thoughts: MutableList<Thought> = mutableListOf()
    private val star = context.resources.getDrawable(R.drawable.ic_star)
    private val starBorder = context.resources.getDrawable(R.drawable.ic_star_border)

    fun setThoughts(thoughts: List<Thought>) {
        this.thoughts.clear()
        this.thoughts.addAll(thoughts)
        notifyDataSetChanged()
    }

    class ThoughtViewHolder(private val context: Context, private val binding: ItemThoughtBinding,
                            private val clickListener: ClickListener<Thought>):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(thought: Thought, star: Drawable) {
            binding.thought.text = thought.thought
            binding.timestamp.text = thought.updatedOn
            binding.star.setImageDrawable(star)
            binding.star.setOnClickListener {
                clickListener.onClick(thought, ClickType.STAR)
            }
            if (!thought.imgSource.isNullOrEmpty()) {
                Glide.with(context)
                        .load(File(thought.imgSource))
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_not_done)
                        .into(binding.img)
            } else {
                binding.img.visibility = View.GONE
            }
            binding.root.setOnClickListener {
                clickListener.onClick(thought, ClickType.THOUGHT)
            }
            binding.root.setOnLongClickListener {
                clickListener.onLongClick(thought)
                false
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThoughtsAdapter.ThoughtViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemThoughtBinding.inflate(inflater, parent, false)
        return ThoughtViewHolder(context, binding, clickListener)
    }

    override fun getItemCount() = thoughts.size

    override fun onBindViewHolder(holder: ThoughtsAdapter.ThoughtViewHolder, position: Int) {
        if (thoughts[position].starred) {
            holder.bind(thoughts[position], star)
        } else {
            holder.bind(thoughts[position], starBorder)
        }
    }
}