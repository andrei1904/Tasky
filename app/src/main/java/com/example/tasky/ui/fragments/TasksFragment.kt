package com.example.tasky.ui.fragments

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasky.R
import com.example.tasky.data.model.entities.Icon
import com.example.tasky.data.model.entities.IconType
import com.example.tasky.data.model.entities.TaskWithSubtasks
import com.example.tasky.databinding.FragmentTasksBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.adapter.TasksAdapter
import com.example.tasky.ui.viewmodels.TasksViewModel
import com.example.tasky.utils.Constants
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable

@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>(), TasksAdapter.TaskClickListener, SearchView.OnQueryTextListener{

    private lateinit var tasksAdapter: TasksAdapter

    private val viewModel by activityViewModels<TasksViewModel>()

    private val disposable = CompositeDisposable()

    var status = Constants.ALL_TASKS

    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentTasksBinding.inflate(inflater, container, false)

        if (rootView == null) {
            rootView = viewBinding!!.root
            isFirstLoaded = true
            tasksAdapter = TasksAdapter(this)
            initRecyclerView()
            loadData()
            scroll()
            initFilter()
            onBackPressListener()

            binding.searchView.setOnQueryTextListener(this)
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
            {showSearchBar()}
        )
    }

    private fun initRecyclerView() {
        val recyclerView = binding.tasksRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = tasksAdapter
    }

    private fun scroll() {
        binding.tasksRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                viewModel.setCurrentPosition(lastPosition)
            }
        })

    }

    private fun loadData(status: String = Constants.ALL_TASKS) {
        disposable.add(
            viewModel.getAllTasks(status)
                .subscribe(
                    { result ->
                        tasksAdapter.setTasksList(result)
                        val pos = viewModel.getCurrentPosition()
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding.tasksRecyclerView.scrollToPosition(pos)
                        }, 200)
                    },
                    { throwable ->
                        Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
                    }
                )
        )
    }

    private fun clearTaskList() {
        tasksAdapter.clearList()
        disposable.clear()
    }

    private fun showSearchBar() {
        if (binding.searchView.visibility == View.GONE) {
            binding.searchView.visibility = View.VISIBLE
            binding.topBar.root.visibility = View.GONE

            binding.searchView.onActionViewExpanded()
        }
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

        binding.filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            binding.filterChipGroup.isEnabled = false
            status = getTaskStatusById(checkedId)
            updateChip(group, checkedId)
            clearTaskList()
            loadData(status)
        }
    }

    private fun updateChip(chipGroup: ChipGroup, chipId: Int) {
        if (chipId != Chip.NO_ID)
            chipGroup.findViewById<Chip>(chipId)?.isChecked = true
    }


    private fun getTaskStatusById(checkedId: Int) = when (checkedId) {
        R.id.new_chip -> Constants.NEW
        R.id.in_progress_chip -> Constants.IN_PROGRESS
        R.id.complete_chip -> Constants.COMPLETE
        R.id.overdue_chip -> Constants.OVERDUE
        else -> Constants.ALL_TASKS
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

    private fun onBackPressListener() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (binding.searchView.visibility == View.VISIBLE) {
                binding.searchView.setQuery("", false)
                binding.searchView.onActionViewCollapsed()

                binding.searchView.visibility = View.GONE
                binding.topBar.root.visibility = View.VISIBLE

            } else {
                (activity as BaseActivity).finish()
            }
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        binding.searchView.clearFocus()
        return true
    }

    override fun onQueryTextChange(searchTerm: String?): Boolean {
        if (searchTerm == null ) return false
        val result = getSearchResult(searchTerm)
        tasksAdapter.setFilteredTasks(result)
        return true

    }

    private fun getSearchResult(searchTerm: String) = tasksAdapter.getTasksList().let { list ->
        val terms = searchTerm.trim().split("\\s+".toRegex())
        if (terms.isEmpty()) {
            list
        } else {
            val result = list.filter { taskWithSubtasks ->
                terms.all { term ->
                    val fields = listOf(taskWithSubtasks.task.title,
                    taskWithSubtasks.task.domain)
                    taskWithSubtasks.task.title.contains(term, ignoreCase = true)
                    fields.any {
                        s -> s.contains(term, ignoreCase = true)
                    }
                }
            }
            result
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

}
