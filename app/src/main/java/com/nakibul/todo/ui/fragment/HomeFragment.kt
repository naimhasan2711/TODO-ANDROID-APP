package com.nakibul.todo.ui.fragment


import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nakibul.todo.R
import com.nakibul.todo.databinding.FragmentHomeBinding
import com.nakibul.todo.ui.adapter.TodoAdapter
import com.nakibul.todo.ui.viewmodel.TodoViewModel
import com.nakibul.todo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nakibul.todo.service.SendBroadcast


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: TodoViewModel by viewModels()
    private val todoAdapter: TodoAdapter by lazy {
        TodoAdapter(requireContext(), viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addTask.setOnClickListener {
            findNavController().navigate(R.id.homeFrg_to_addFrag)
        }
        initRecyclerView()
        initObservers()
        isFirstTimeInstalled()
    }

    //checking if app is installed first time or not
    private fun isFirstTimeInstalled() {
        val settings = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor: SharedPreferences.Editor = settings.edit()
        val firstStart = settings.getBoolean(Constants.SHARED_KEY, true)
        if (firstStart) {
            Timber.e(getString(R.string.first_time))
            viewModel.insertApiDataToLocalDb()
            editor.putBoolean(Constants.SHARED_KEY, false)
            editor.apply()
        }
    }

    //binding recyclerview
    private fun initRecyclerView() {
        binding.todoRecyclerview.apply {
            val currentOrientation = resources.configuration.orientation
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                // Landscape
                val staggeredGridLayoutManager =
                    StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                binding.todoRecyclerview.layoutManager = staggeredGridLayoutManager

            } else {
                // Portrait
                layoutManager = LinearLayoutManager(context)
            }
            adapter = todoAdapter
        }
        viewModel.todoListLocal.observe(viewLifecycleOwner) {
            todoAdapter.addTask(it)
        }
    }

    //observing broadcast
    private fun initObservers() {
        viewModel.todoListLocal.observe(viewLifecycleOwner) {
            todoAdapter.addTask(it)
            SendBroadcast.sendUpdateBroadcast(requireContext())
        }

    }

}