package com.luizcampos.exactachallenge.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luizcampos.exactachallenge.R
import com.luizcampos.exactachallenge.adapter.TagsAdapter
import com.luizcampos.exactachallenge.databinding.FragmentExpenseEntryBinding
import com.luizcampos.exactachallenge.helper.MoneyTextWatcher
import com.luizcampos.exactachallenge.helper.Utils
import com.luizcampos.exactachallenge.model.Tags
import com.luizcampos.exactachallenge.model.database.AppDatabase
import com.luizcampos.exactachallenge.repository.ExpenseDbDataSource
import com.luizcampos.exactachallenge.viewmodel.ExpenseViewModel
import com.luizcampos.exactachallenge.viewmodel.ExpenseViewModelFactory
import com.luizcampos.exactachallenge.viewmodel.registration.RegistrationViewParams
import java.util.*

class ExpenseEntryFragment : Fragment(R.layout.fragment_expense_entry) {
    private lateinit var binding: FragmentExpenseEntryBinding

    private lateinit var tagsAdapter: TagsAdapter

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
        binding = FragmentExpenseEntryBinding.inflate(inflater, container, false)

        expenseViewModel.tags.observe(viewLifecycleOwner) { tags ->
            val rvpValue = recoveryData(RegistrationViewParams::class.java.simpleName)
            if (!rvpValue.isNullOrBlank()) {
                val registrationViewParams = Gson().fromJson(rvpValue, RegistrationViewParams::class.java) as RegistrationViewParams

                registrationViewParams.apply {
                    binding.edtName.setText(name)
                    binding.toolbar.subtitle = Utils.getDate(debtDate.toLong())
                    binding.edtNote.setText(description)
                    if (amount
                            .replace(".", "")
                            .replace(",", "")
                            .toInt() > 0
                    )
                        binding.edtAmount.setText(MoneyTextWatcher.toFormat(amount))
                }
            }

            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                gsonSubmitList(recoveryData(TagsAdapter::class.java.simpleName))
                val list = tagsAdapter.currentList.toMutableList()

                when(list.contains(tags)) {
                    true -> {
                        list.remove(tags)
                    }
                    false -> {
                        list.add(tags)
                    }
                }

                tagsAdapter.submitList(list)
                saveData(list)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (binding.toolbar.subtitle == null || binding.toolbar.subtitle.toString().isNullOrBlank()) {
            binding.toolbar.subtitle = Utils.getDate(System.currentTimeMillis())
        }

        binding.toolbar.setNavigationOnClickListener {
            clearData()
            findNavController().navigate(R.id.action_expenseEntryFragment_to_mainFragment)
        }

        expenseViewModel.currentEntryTime.observe(viewLifecycleOwner) { time ->
            binding.toolbar.subtitle = Utils.getDate(time)
        }

        binding.toolbar.setOnMenuItemClickListener menu@{ menuItem ->
            when (menuItem.itemId) {
                R.id.calendar -> {
                    expenseViewModel.onDateSelect()
                }
                R.id.time -> {
                    expenseViewModel.onTimeSelect()
                }
            }
            return@menu true
        }

        expenseViewModel.onChooseDateShow.observe(viewLifecycleOwner) { calendar ->
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                showDatePicker(calendar)
            }
        }

        expenseViewModel.onChooseTimeShow.observe(viewLifecycleOwner) { calendar ->
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                showTimePickerDialog(calendar)
            }
        }

        binding.cvAddLabel.setOnClickListener {
            saveData(tagsAdapter.currentList)
            findNavController().navigate(R.id.action_expenseEntryFragment_to_bottomSheetTagsFragment)
        }

        initRecyclerView()

        binding.edtAmount.addTextChangedListener(MoneyTextWatcher(binding.edtAmount))

        binding.btnSave.setOnClickListener {
            expenseViewModel.saveExpense(
                RegistrationViewParams(
                    name = binding.edtName.text.toString(),
                    amount = MoneyTextWatcher.parseCurrencyValue(binding.edtAmount.text.toString()).toString(),
                    debtDate = expenseViewModel.currentEntryTime.value.toString(),
                    tags =  Utils.tagsToGson(tagsAdapter.currentList),
                    description = binding.edtNote.text.toString()
                )
            )
            clearData()
            findNavController().navigate(R.id.action_expenseEntryFragment_to_mainFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideInputKeyboard()
    }

    private fun hideInputKeyboard() {
        val inputMethodManager =
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        inputMethodManager?.hideSoftInputFromWindow(binding.edtName.windowToken, 0)
    }

    private fun saveData(list: List<Tags>?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        val gson = Gson()

        val valueTags = gson.toJson(list?.toList() ?: tagsAdapter.currentList.toMutableList())
        val valueRegistrationViewParams = gson.toJson(
            RegistrationViewParams(
                name = binding.edtName.text.toString(),
                amount = MoneyTextWatcher.parseCurrencyValue(binding.edtAmount.text.toString()).toString(),
                debtDate = expenseViewModel.currentEntryTime.value.toString(),
                tags =  Utils.tagsToGson(tagsAdapter.currentList),
                description = binding.edtNote.text.toString()
            )
        )

        with(sharedPref.edit()) {
            putString(TagsAdapter::class.java.simpleName, valueTags)
            putString(RegistrationViewParams::class.java.simpleName, valueRegistrationViewParams)
            apply()
        }
    }

    private fun recoveryData(key: String) : String? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return null
        return sharedPref.getString(key, "")
    }

    private fun clearData() {
        tagsAdapter.currentList.toMutableList().clear()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(TagsAdapter::class.java.simpleName, "")
            putString(RegistrationViewParams::class.java.simpleName, "")
            apply()
        }
    }


    private fun initRecyclerView() {
        val value = recoveryData(TagsAdapter::class.java.simpleName)

        tagsAdapter = TagsAdapter(TagsAdapter.Orientation.Horizontal)

        gsonSubmitList(value)

        binding.rvTagsCategory.adapter = tagsAdapter
    }

    private fun gsonSubmitList(value: String?) {
        val gson = Gson()

        if (!value.isNullOrBlank()) {
            val listType = object : TypeToken<List<Tags>>() {}.type
            tagsAdapter.submitList(gson.fromJson(value, listType))
        }
    }

    private fun showTimePickerDialog(calendar: Calendar) {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                expenseViewModel.selectTime(hourOfDay, minute, 0)
            },
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            false
        ).show()
    }

    private fun showDatePicker(time: Calendar) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                time.set(year, month, dayOfMonth)
                expenseViewModel.selectDateTime(time.timeInMillis)
            },
            time[Calendar.YEAR],
            time[Calendar.MONTH],
            time[Calendar.DAY_OF_MONTH]
        ).show()
    }
}