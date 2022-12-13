package com.luizcampos.exactachallenge.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.luizcampos.exactachallenge.R
import com.luizcampos.exactachallenge.adapter.ExpenseAdapter
import com.luizcampos.exactachallenge.databinding.FragmentMainBinding
import com.luizcampos.exactachallenge.helper.DatabaseEvent
import com.luizcampos.exactachallenge.helper.ToastDurationProvider
import com.luizcampos.exactachallenge.model.Expense
import com.luizcampos.exactachallenge.repository.ToastRepository
import com.luizcampos.exactachallenge.viewmodel.ExpenseViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var binding: FragmentMainBinding

    private lateinit var expenseAdapter: ExpenseAdapter

    private val viewModel: ExpenseViewModel by activityViewModels()

    @Inject
    lateinit var duration: ToastDurationProvider

    @Inject
    lateinit var toast: ToastRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        lifecycleScope.launchWhenCreated {
            createMenu()

            binding.fbMainAdd.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_expenseModalFragment)
            }

            initRecyclerView()

            viewModel.expenses.collect { event ->
                when(event) {
                    is DatabaseEvent.Success<*> -> {
                        val list: List<Expense> = if (event.value is Expense) {
                            listOf(event.value)
                        } else {
                            event.value as List<Expense>
                        }

                        expenseAdapter.submitList(list)
                    }
                    is DatabaseEvent.Error -> {
                        toast.error(event.message, duration.LENGTH_SHORT)
                    }
                    is DatabaseEvent.Loading -> {
                        toast.info(getString(R.string.loading_message), duration.LENGTH_1S)
                    }
                    else -> Unit
                }
            }
        }

        return binding.root
    }

    private fun createMenu() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ic_exacta_menu -> {
                    viewModel.getExpenses()
                }
            }
            true
        }

        binding.toolbar.inflateMenu(R.menu.menu_search_id_exacta)
        val manager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = binding.toolbar.menu.findItem(R.id.ic_search_menu)
        val searchView = searchItem?.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(activity?.componentName))

        searchView.inputType = InputType.TYPE_CLASS_NUMBER

        // workaround https://itecnote.com/tecnote/android-change-appcompats-searchview-text-and-hint-color/
        val textArea: SearchView.SearchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete
        textArea.hint = getString(R.string.search_id)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()

                when(query.isNullOrBlank()) {
                    true -> {
                        viewModel.getExpenses()
                    }
                    false -> {
                        viewModel.getExpense(query.toLong())
                    }
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun initRecyclerView() {
        expenseAdapter = ExpenseAdapter { id ->
            viewModel.deleteExpense(id)
        }
        binding.rvExpenses.adapter = expenseAdapter

        viewModel.getExpenses()
    }
}