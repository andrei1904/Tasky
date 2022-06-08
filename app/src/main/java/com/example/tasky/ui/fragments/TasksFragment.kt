package com.example.tasky.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasky.R
import com.example.tasky.data.model.entities.Icon
import com.example.tasky.data.model.entities.IconType
import com.example.tasky.data.model.entities.TaskWithSubtasks
import com.example.tasky.databinding.FragmentTasksBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.adapter.TasksAdapter
import com.example.tasky.ui.viewmodels.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable

@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>(), TasksAdapter.TaskClickListener {

    private lateinit var tasksAdapter: TasksAdapter

    private val viewModel by viewModels<TasksViewModel>()

    private val disposable = CompositeDisposable()

    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentTasksBinding.inflate(inflater, container, false)

        if (rootView == null) {
            rootView = viewBinding!!.root
            isFirstLoaded = true
            tasksAdapter = TasksAdapter(this)
            initRecyclerView()
            initFilter()
            loadData()
        } else {
            isFirstLoaded = false
        }
    }

    override fun getTitle(): String {
        return getString(R.string.tasks)
    }

    override fun getRightIcons(): List<Icon> {
        return listOf(Icon(
            IconType.ADD_ICON,
            {
                (activity as BaseActivity).getFragmentNavigation()
                    .replaceFragment(CreateTaskFragment())
            }
        ))
    }

    override fun getLeftIcon(): Icon {
        return Icon(
            IconType.SEARCH_ICON,
            {}
        )
    }

    private fun initRecyclerView() {
        val recyclerView = binding.tasksRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = tasksAdapter
    }

    private fun loadData() {
        disposable.add(
            viewModel.getAllTasks()
                .subscribe(
                    { result ->
                        tasksAdapter.setTasksList(result)
                    },
                    { throwable ->
                        Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
                    }
                )
        )
    }

    private fun initFilter() {
        binding.filterArrow.setOnClickListener {
            if (binding.filterChipGroup.visibility == View.GONE) {
                binding.filterChipGroup.visibility = View.VISIBLE
                binding.filterArrow.setImageDrawable(
                    context?.let { it1 -> AppCompatResources.getDrawable(it1, R.drawable.ic_baseline_arrow_drop_up_24) }
                )

            } else if (binding.filterChipGroup.visibility == View.VISIBLE) {
                binding.filterChipGroup.visibility = View.GONE
                binding.filterArrow.setImageDrawable(
                    context?.let { it1 -> AppCompatResources.getDrawable(it1, R.drawable.ic_baseline_arrow_drop_down_24) }
                )
            }
        }
    }
    override fun onDeleteClicked(taskId: Long, position: Int) {
        disposable.add(
            viewModel.deleteTaskById(taskId)
                .subscribe(
                    { result ->
                        if (result) {
                            tasksAdapter.notifyItemDeleted(position)
                        }
                    },
                    { throwable ->
                        Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
                    }
                )
        )
    }

    override fun onTaskClicked(taskWithSubtasks: TaskWithSubtasks) {
        (activity as BaseActivity).getFragmentNavigation()
            .replaceFragment(TaskFragment(taskWithSubtasks))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
