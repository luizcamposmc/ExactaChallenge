package com.luizcampos.exactachallenge.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.luizcampos.exactachallenge.R
import com.luizcampos.exactachallenge.adapter.ExpenseAdapter
import com.luizcampos.exactachallenge.databinding.FragmentMainBinding
import com.luizcampos.exactachallenge.model.database.AppDatabase
import com.luizcampos.exactachallenge.repository.ExpenseDbDataSource
import com.luizcampos.exactachallenge.viewmodel.ExpenseViewModel
import com.luizcampos.exactachallenge.viewmodel.ExpenseViewModelFactory

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding

    private lateinit var expenseAdapter: ExpenseAdapter

    private val expenseViewModel: ExpenseViewModel by activityViewModels(
        factoryProducer = {
            val database = AppDatabase.getDatabase(requireContext())

            ExpenseViewModelFactory(
                expenseRepository = ExpenseDbDataSource(database.expenseDao())
            )
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createMenu()

        binding.fbMainAdd.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_expenseModalFragment)
        }

        expenseViewModel.expenses.observe(viewLifecycleOwner) { listExpense ->
            expenseAdapter.submitList(listExpense)
        }

        initRecyclerView()
    }

    private fun createMenu() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ic_exacta_menu -> {
                    expenseViewModel.getExpenses()
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
                        expenseViewModel.getExpenses()
                    }
                    false -> {
                        expenseViewModel.getExpense(query.toLong())
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
            expenseViewModel.deleteExpense(id)
        }
        binding.rvExpenses.adapter = expenseAdapter

        expenseViewModel.getExpenses()
    }
}