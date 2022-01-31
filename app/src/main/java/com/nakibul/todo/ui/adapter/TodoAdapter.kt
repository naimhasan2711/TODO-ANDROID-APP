package com.nakibul.todo.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

import com.nakibul.todo.R
import com.nakibul.todo.data.local.Todo
import com.nakibul.todo.databinding.TaskLayoutBinding
import com.nakibul.todo.ui.fragment.HomeFragmentDirections
import com.nakibul.todo.ui.viewmodel.TodoViewModel
import java.text.SimpleDateFormat

class TodoAdapter(
    val context: Context, val viewModel: TodoViewModel
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private val tasks = mutableListOf<Todo>()

    @SuppressLint("NotifyDataSetChanged")
    fun addTask(task: List<Todo>) {
        tasks.clear()
        tasks.addAll(task)
        notifyDataSetChanged()
    }

    inner class TodoViewHolder(private val binding: TaskLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Todo) {
            //making a string from list
            var subTodo = ""
            for (todo in task.todo) {
                subTodo += "✔ $todo\n"
            }
            binding.textviewTitle.text = task.title
            binding.textViewTodo.text = subTodo
            binding.textViewTime.text = SimpleDateFormat("EE dd MMM yyyy hh:mm a").format(task.time)
            val action = HomeFragmentDirections.homeFragToUpdateFrag(task)

            //option button listener in recyclerview
            binding.imageViewOption.setOnClickListener {
                val popupMenu = PopupMenu(context, binding.imageViewOption)
                popupMenu.menuInflater.inflate(R.menu.option_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->

                    when (item.itemId) {
                        R.id.update_menu ->
                            //navigate to update fragment
                            binding.root.findNavController().navigate(action)
                        R.id.delete_menu ->
                            //delete task
                            viewModel.deleteTask(task)
                    }
                    true
                }
                popupMenu.show()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.TodoViewHolder {
        val bind = TaskLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(bind)
    }

    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        holder.bind(tasks[position])

    }

    override fun getItemCount() = tasks.size

}

