package com.luizcampos.exactachallenge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luizcampos.exactachallenge.databinding.ItemExpenseMainBinding
import com.luizcampos.exactachallenge.helper.MoneyTextWatcher
import com.luizcampos.exactachallenge.helper.Utils
import com.luizcampos.exactachallenge.model.Expense

class ExpenseAdapter(private val onTrashClickListener: (Long) -> Unit = {}) : ListAdapter<Expense, ExpenseAdapter.ExpenseAdapterHolder>(DIFF_CALLBACK){

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Expense>() {
            override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseAdapterHolder {
        return ExpenseAdapterHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ExpenseAdapterHolder, position: Int) {
        return holder.bind(getItem(position), onTrashClickListener)
    }

    class ExpenseAdapterHolder (
        private val itemExpenseMainBinding: ItemExpenseMainBinding
    ) : RecyclerView.ViewHolder(itemExpenseMainBinding.root) {
        fun bind(expense: Expense, onTrashClickListener: (Long) -> Unit = {}) {
            val tagsAdapter = TagsAdapter(TagsAdapter.Orientation.Horizontal)
            itemExpenseMainBinding.run {
                tvIdValue.text = expense.id
                tvName.text = expense.name
                tvCalendar.text = Utils.getDate(expense.debtDate.toLong())
                tvDescription.text = expense.description
                tvAmount.text = MoneyTextWatcher.toFormat(expense.amount)
                rvTagsExpenseMain.adapter = tagsAdapter
                tagsAdapter.submitList(Utils.gsonToTags(expense.tags))
                imvTrash.isClickable = true
                imvTrash.setOnClickListener {
                    onTrashClickListener(expense.id.toLong())
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup) : ExpenseAdapterHolder {
                val itemExpenseMainBinding = ItemExpenseMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ExpenseAdapterHolder(itemExpenseMainBinding)
            }
        }
    }
}