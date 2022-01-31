package com.nakibul.todo.ui.fragment


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
import com.nakibul.todo.R
import com.nakibul.todo.data.local.Todo
import com.nakibul.todo.databinding.FragmentAddBinding
import com.nakibul.todo.ui.activity.MainActivity
import com.nakibul.todo.ui.viewmodel.TodoViewModel
import com.nakibul.todo.utils.Converters
import dagger.hilt.android.AndroidEntryPoint
import jhonatan.sabadi.datetimepicker.showDateAndTimePicker
import java.util.*


@AndroidEntryPoint
class AddFragment : Fragment() {

    private var timeFromPicker: Long = 0
    private lateinit var binding: FragmentAddBinding
    private val todoViewModel: TodoViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleFocusListener()
        descriptionFocusListener()

        binding.textView.text =
            Converters.timeToString(Calendar.getInstance().timeInMillis)

        //time picker listener
        binding.textView.setOnClickListener {
            timePicker()
        }

        //add task button listener
        binding.buttonAdd.setOnClickListener {

            val title = binding.edittextTitle.text.toString()
            val subTodoList: List<String> =
                binding.edittextDescription.text.toString().split("\n").dropLastWhile { it == "" }

            //checking for empty fields
            if (!TextUtils.isEmpty(title) && subTodoList.isNotEmpty() && timeFromPicker >= Calendar.getInstance().timeInMillis) {

                val insertItem =
                    Todo(UUID.randomUUID().toString(), timeFromPicker, title, subTodoList)
                todoViewModel.insertNewTodo(insertItem)
                binding.edittextTitle.setText("")
                binding.edittextDescription.setText("")
                findNavController().navigate(R.id.addFrag_to_homeFrag)
            } else {
                Toast.makeText(context, getString(R.string.fill_all_properly), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    //title input checking
    private fun titleFocusListener() {
        binding.edittextTitle.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.edittextTitleContainer.helperText = validTitle()
            }
        }
    }

    //description input checking
    private fun descriptionFocusListener() {
        binding.edittextDescription.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.edittextDescriptionContainer.helperText = validDescription()
            }
        }
    }

    private fun validTitle(): String? {
        val titleText = binding.edittextTitle.text.toString()
        if (titleText == "") {
            return getString(R.string.title_cannot_be_null)
        }
        return null
    }

    private fun validDescription(): String? {
        val titleText = binding.edittextDescription.text.toString()
        if (titleText == "") {
            return getString(R.string.desc_cannot_be_null)
        }
        return null
    }

    //picking time
    @RequiresApi(Build.VERSION_CODES.N)
    private fun timePicker() {
        (requireActivity() as MainActivity).showDateAndTimePicker { date: Date ->
            if (date.time <= Calendar.getInstance().timeInMillis) {
                Toast.makeText(context, getString(R.string.select_a_date_future), Toast.LENGTH_LONG)
                    .show()
            } else {
                binding.textView.text = Converters.timeToString(date.time)
                timeFromPicker = date.time
            }

        }
    }

}