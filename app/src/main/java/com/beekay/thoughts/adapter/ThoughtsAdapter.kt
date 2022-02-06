package com.beekay.thoughts.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.beekay.thoughts.R
import com.beekay.thoughts.databinding.ItemThoughtBinding
import com.beekay.thoughts.model.Thought
import com.bumptech.glide.Glide
import java.io.File

/**
 * Created by Krishna by 16-11-2020
 */
class ThoughtsAdapter(
    private val context: Context,
    private val clickListener: ClickListener<Thought>
) :
    RecyclerView.Adapter<ThoughtsAdapter.ThoughtViewHolder>() {

    val thoughts: MutableList<Thought> = mutableListOf()
    private val star = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_star, null)!!
    private val starBorder =
        ResourcesCompat.getDrawable(context.resources, R.drawable.ic_star_border, null)!!

    @SuppressLint("NotifyDataSetChanged")
    fun setThoughts(thoughts: List<Thought>) {
        this.thoughts.clear()
        this.thoughts.addAll(thoughts)
        notifyDataSetChanged()
    }

    class ThoughtViewHolder(
        private val context: Context, private val binding: ItemThoughtBinding,
        private val clickListener: ClickListener<Thought>
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(thought: Thought, star: Drawable) {
            binding.thought.text = thought.thought
            binding.timestamp.text = thought.createdOn.substringBeforeLast(':')
            binding.star.setImageDrawable(star)
            if (thought.selected) {
                binding.selectIndicator.visibility = View.VISIBLE
            } else {
                binding.selectIndicator.visibility = View.GONE
            }
            binding.star.setOnClickListener {
                clickListener.onClick(thought, ClickType.STAR)
            }
            if (!thought.imgSource.isNullOrEmpty()) {
                val imagesDir = File(context.filesDir, "images")
                Glide.with(context)
                    .load(File(imagesDir, thought.imgSource))
                    .placeholder(R.drawable.ic_not_done)
                    .error(R.drawable.ic_not_done)
                    .into(binding.img)
            } else {
                binding.img.visibility = View.GONE
            }
            binding.root.setOnClickListener {
                clickListener.onClick(thought, ClickType.THOUGHT)
            }
            binding.root.setOnLongClickListener {
                clickListener.onLongClick(thought, this)
                true
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ThoughtViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemThoughtBinding.inflate(inflater, parent, false)
        return ThoughtViewHolder(context, binding, clickListener)
    }

    override fun getItemCount() = thoughts.size

    override fun onBindViewHolder(holder: ThoughtViewHolder, position: Int) {
        if (thoughts[position].starred) {
            holder.bind(thoughts[position], star)
        } else {
            holder.bind(thoughts[position], starBorder)
        }
    }
}