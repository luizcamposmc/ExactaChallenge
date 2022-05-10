package com.luizcampos.exactachallenge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luizcampos.exactachallenge.R
import com.luizcampos.exactachallenge.databinding.ItemCategoryBinding
import com.luizcampos.exactachallenge.model.Tags

class TagsAdapter (private val orientation: Orientation) : ListAdapter<Tags, TagsAdapter.TagsAdapterHolder>(DIFF_CALLBACK) {

    private var _tagsClickListener: (Tags) -> Unit = {}
    var tagsClickListener: (Tags) -> Unit
        set(value) {
            _tagsClickListener = value
        }
        get() = _tagsClickListener

    enum class Orientation {
        Horizontal,
        Vertical;
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Tags>() {
            override fun areContentsTheSame(oldItem: Tags, newItem: Tags): Boolean {
                return oldItem.category == newItem.category
            }

            override fun areItemsTheSame(oldItem: Tags, newItem: Tags): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsAdapterHolder {
        return TagsAdapterHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TagsAdapterHolder, position: Int) {
        return holder.bind(getItem(position), orientation, _tagsClickListener)
    }

    class TagsAdapterHolder(
        private val itemCategoryBinding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(itemCategoryBinding.root){
        fun bind(tags: Tags, orientation: Orientation, tagsClickListener: (Tags) -> Unit) {
            itemCategoryBinding.run {
                tvName.text = tags.category

                if (orientation == Orientation.Vertical) {
                    cdCategory.isClickable = true
                    cdCategory.setOnClickListener {
                        tagsClickListener(tags)
                    }
                }

                val params = cdCategory.layoutParams as ViewGroup.MarginLayoutParams
                val marginValue = root.resources.getDimension(R.dimen.grid_3).toInt()

                when(orientation == Orientation.Horizontal) {
                    true -> {
                        params.setMargins(
                            params.leftMargin,
                            params.topMargin,
                            marginValue,
                            params.bottomMargin
                        )
                    }
                    false -> {
                        params.setMargins(
                            when(tags.id % 2 == 0){
                                true -> {
                                    marginValue * 2
                                }
                                false -> {
                                    params.leftMargin
                                }
                            },
                            params.topMargin,
                            when(tags.id % 2 == 1){
                                true -> {
                                    marginValue * 4
                                }
                                false -> {
                                    params.rightMargin
                                }
                            },
                            marginValue
                        )
                    }
                }
                cdCategory.layoutParams = params
            }
        }

        companion object {
            fun create(parent: ViewGroup) : TagsAdapterHolder {
                val itemCategoryBinding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return TagsAdapterHolder(itemCategoryBinding)
            }
        }
    }
}