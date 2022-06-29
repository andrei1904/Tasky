package com.example.tasky.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasky.data.model.entities.Icon
import com.example.tasky.data.model.entities.IconType
import com.example.tasky.data.model.entities.Subtask
import com.example.tasky.data.model.entities.TaskWithSubtasks
import com.example.tasky.databinding.FragmentTaskBinding
import com.example.tasky.ui.activites.MainActivity
import com.example.tasky.ui.adapter.TaskSubtasksAdapter
import com.example.tasky.ui.viewmodels.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskFragment(private val taskWithSubtasks: TaskWithSubtasks) :
    BaseFragment<FragmentTaskBinding>(), TaskSubtasksAdapter.Listener {

    private lateinit var taskSubtasksAdapter: TaskSubtasksAdapter

    private val viewModel by viewModels<TasksViewModel>()

    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentTaskBinding.inflate(inflater, container, false)

        if (rootView == null) {
            rootView = viewBinding!!.root
            isFirstLoaded = true
            onBackPressListener()
        } else {
            isFirstLoaded = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isFirstLoaded) {
            initData()
        }
    }

    override fun getRightIcons(): List<Icon?> {
        return listOf()
    }

    override fun getLeftIcon(): Icon {
        return Icon(
            IconType.BACK_ICON,
            {
                (activity as MainActivity).onBackPressTask()
            }
        )
    }

    override fun getTitle(): String {
        return TITLE
    }

    private fun initData() {
        taskSubtasksAdapter = TaskSubtasksAdapter(this)
        initRecyclerView()

        taskSubtasksAdapter.setData(taskWithSubtasks)
    }

    private fun initRecyclerView() {
        val recyclerView = binding.tasksRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = taskSubtasksAdapter
    }

    override fun onTaskProgressUpdated(taskId: Long, progress: Int) {
        viewModel.updateProgress(taskId, progress)
            .subscribe({

            }, {})
    }

    override fun onStopTimeClicked(taskId: Long, spentTime: Long) {
        viewModel.updateTimeSpent(taskId, spentTime)
            .subscribe({

            }, { throwable ->
                Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
            })
    }

    override fun onSubtaskStatusChange(subtask: Subtask) {
        viewModel.updateSubtask(subtask)
            .subscribe({

            }, { throwable ->
                Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
            })
    }

    private fun onBackPressListener() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            (activity as MainActivity).onBackPressTask()
        }
    }

    companion object {
        private const val TITLE = "Task details"
    }
}