package com.example.tasky.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasky.R
import com.example.tasky.data.model.Icon
import com.example.tasky.data.model.IconType
import com.example.tasky.data.model.Resource
import com.example.tasky.data.model.enums.Status
import com.example.tasky.databinding.FragmentCreateTaskBinding
import com.example.tasky.databinding.FragmentTasksBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.adapter.TasksAdapter
import com.example.tasky.ui.viewmodels.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>(), TasksAdapter.DeleteTaskClickListener {

    private lateinit var tasksAdapter: TasksAdapter

    private val viewModel by viewModels<TasksViewModel>()

    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentTasksBinding.inflate(inflater, container, false)

        if (rootView == null) {
            rootView = viewBinding!!.root
            isFirstLoaded = true
        } else {
            isFirstLoaded = false
        }
    }

    override fun getTitle(): String {
        return getString(R.string.tasks)
    }

    override fun getRightIcon(): Icon {
        return Icon(
                IconType.ADD_ICON,
                {
                    (activity as BaseActivity).getFragmentNavigation()
                        .replaceFragment(CreateTaskFragment())
                }
            )
    }

    override fun getLeftIcon(): Icon? {
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tasksAdapter = TasksAdapter(this)
        initRecyclerView()


        viewModel.getAllTasks().observe(viewLifecycleOwner) { result ->
            if (result.status == Status.SUCCESS) {
                result.data?.let { tasksAdapter.setTasksList(it) }
            } else {
                Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView() {
        val recyclerView = binding.tasksRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = tasksAdapter
    }

    override fun onDeleteClicked(taskId: Long, position: Int) {
//        viewModel.deleteTaskById(taskId).observe(viewLifecycleOwner) { result ->
//            if (result.status == Status.SUCCESS && result.data == true) {
//                // SE APELEAZA MULTIPLU ...
//                Log.d("okoko", taskId.toString());
//                tasksAdapter.notifyItemDeleted(position)
//            } else {
//                Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show()
//            }
//        }

        viewModel.deleteTaskId(taskId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    if (result > 0) {
                        tasksAdapter.notifyItemDeleted(position)
                    }
                },
                {
                    Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show()
                }
            )
    }
}
