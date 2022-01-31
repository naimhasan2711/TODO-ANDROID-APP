package com.nakibul.todo.ui.fragment

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nakibul.todo.R
import com.nakibul.todo.data.local.Todo
import com.nakibul.todo.databinding.FragmentUpdateBinding
import com.nakibul.todo.ui.activity.MainActivity
import com.nakibul.todo.ui.viewmodel.TodoViewModel
import com.nakibul.todo.utils.Constants
import com.nakibul.todo.utils.Converters
import dagger.hilt.android.AndroidEntryPoint
import jhonatan.sabadi.datetimepicker.showDateAndTimePicker
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
class UpdateFragment : Fragment() {

    private lateinit var binding: FragmentUpdateBinding
    private val args by navArgs<UpdateFragmentArgs>()
    private val viewModel: TodoViewModel by viewModels()
    private var timeFromPicker: Long = 0

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //making a string from List
        var subTodos = ""
        for (subTodo in args.todoItem.todo) {
            subTodos += subTodo + "\n"
        }
        binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.textViewEditTitle.setText(args.todoItem.title)
        binding.textViewEditDescription.setText(subTodos)
        binding.textViewEditTime.text =
            SimpleDateFormat(Constants.TIME_DATE_FORMAT).format(args.todoItem.time)

        //picking time from time picker
        binding.textViewEditTime.setOnClickListener {
            timePicker()
        }

        //update database button
        binding.buttonUpdate.setOnClickListener {
            updateDB()
            subTodos.none()
        }

        //title input text change listener
        titleFocusListener()
        //title input text change listener
        descriptionFocusListener()
        return binding.root
    }


    //update database
    private fun updateDB() {
        val title = binding.textViewEditTitle.text
        val time = binding.textViewEditTime.text
        val subTodoList: List<String> =
            binding.textViewEditDescription.text.toString().split("\n").dropLastWhile { it == "" }
        Timber.d(subTodoList.toString())
        val timeFromList = Converters.timeToString(args.todoItem.time)

        val task: Todo = if (timeFromList.equals(binding.textViewEditTime)) {
            Todo(args.todoItem.id, args.todoItem.time, title.toString(), subTodoList)
        } else {
            val timeFromPickerUpdated =
                SimpleDateFormat(Constants.TIME_DATE_FORMAT).parse(time.toString()).time
            Todo(args.todoItem.id, timeFromPickerUpdated, title.toString(), subTodoList)
        }

        //checking for empty fields
        if (!TextUtils.isEmpty(task.title) && subTodoList.isNotEmpty() && task.time >= Calendar.getInstance().timeInMillis && (task.time != args.todoItem.time || task.title != args.todoItem.title || subTodoList != args.todoItem.todo)) {
            viewModel.updateTask(task)
            findNavController().navigate(R.id.updateFrag_to_homeFrag)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.fill_all_properly_or_update),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    //title text input field empty checking
    private fun titleFocusListener() {
        binding.textViewEditTitle.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.textviewTitleContainer.helperText = validTitle()
            }
        }
    }

    //description text input field empty checking
    private fun descriptionFocusListener() {
        binding.textViewEditDescription.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.textviewDescriptionContainer.helperText = validDescription()
            }
        }
    }

    //title validation
    private fun validTitle(): String? {
        val titleText = binding.textViewEditTitle.text.toString()
        if (titleText == "") {
            return getString(R.string.title_cannot_be_null)
        }
        return null
    }

    //description validation
    private fun validDescription(): String? {
        val titleText = binding.textViewEditDescription.text.toString()
        if (titleText == "") {
            return getString(R.string.desc_cannot_be_null)
        }
        return null
    }

    //time picker
    @RequiresApi(Build.VERSION_CODES.N)
    private fun timePicker() {
        (requireActivity() as MainActivity).showDateAndTimePicker { date: Date ->
            if (date.time <= Calendar.getInstance().timeInMillis) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.select_a_date_future),
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                val x = SimpleDateFormat(Constants.TIME_DATE_FORMAT).format(date.time)
                binding.textViewEditTime.text = x.toString()
                timeFromPicker = date.time
            }
        }
    }
}