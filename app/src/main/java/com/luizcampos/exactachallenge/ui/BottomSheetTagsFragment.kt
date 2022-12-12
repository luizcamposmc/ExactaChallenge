package com.luizcampos.exactachallenge.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.luizcampos.exactachallenge.R
import com.luizcampos.exactachallenge.adapter.TagsAdapter
import com.luizcampos.exactachallenge.databinding.FragmentBottomSheetTagsBinding
import com.luizcampos.exactachallenge.model.Tags
import com.luizcampos.exactachallenge.viewmodel.ExpenseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetTagsFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetTagsBinding

    private lateinit var tagsAdapter: TagsAdapter

    private val expenseViewModel: ExpenseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetTagsBinding.inflate(inflater, container, false)

        lifecycleScope.launchWhenCreated {
            initRecyclerView()
        }

        return binding.root
    }

    // workaround to put transparent color: https://stackoverflow.com/a/46469709/4743011
    override fun getTheme(): Int {
        return R.style.Theme_ExactaChallenge_BottomSheetDialog
    }

    private fun initRecyclerView() {
        tagsAdapter = TagsAdapter(TagsAdapter.Orientation.Vertical)
        tagsAdapter.tagsClickListener = { tags ->
            expenseViewModel.setTags(tags)
            findNavController().navigate(R.id.action_bottomSheetTagsFragment_to_expenseEntryFragment)
        }

        binding.rvTagsCategory.apply {
            adapter = tagsAdapter
            addItemDecoration(
                ItemDecoration(2, 50, true)
            )
        }

        tagsAdapter.submitList(
            resources.getStringArray(R.array.tagsCategory).mapIndexed { index,  tagsCategory ->
                Tags(index, tagsCategory)
            }
        )
    }

    // workaround to justify gridlayoutmanager: https://stackoverflow.com/a/30701422/4743011
    private class ItemDecoration (
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            when(includeEdge) {
                true -> {
                    outRect.left = spacing - (column * spacing / spanCount)
                    outRect.right = (column + 1) * spacing / spanCount

                    if (position < spanCount)
                        outRect.top = spacing
                }
                false -> {
                    outRect.left = column * spacing / spanCount
                    outRect.right = spacing - (column + 1) * spacing / spanCount

                    if (position >= spanCount)
                        outRect.top = spacing
                }
            }
        }
    }
}