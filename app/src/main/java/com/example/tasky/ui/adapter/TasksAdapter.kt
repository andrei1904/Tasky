package com.example.tasky.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tasky.R
import com.example.tasky.data.model.entities.TaskWithSubtasks
import com.example.tasky.data.model.enums.Status
import com.example.tasky.databinding.ItemTaskBinding
import com.example.tasky.utils.DateFormater
import com.google.android.material.bottomsheet.BottomSheetDialog

class TasksAdapter(
    private val listener: TaskClickListener
) : RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {

    private var tasksList = ArrayList<TaskWithSubtasks>()
    private var allTasks = ArrayList<TaskWithSubtasks>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TasksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        )

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val taskWithSubtasks = tasksList[position]

        holder.binding.textViewTaskDomain.text = taskWithSubtasks.task.domain

        holder.binding.textViewTaskTitle.text = taskWithSubtasks.task.title

        holder.binding.textViewDeadline.text =
            DateFormater.getDateTimeFromMillis(taskWithSubtasks.task.deadline)

        if (taskWithSubtasks.task.imposedDeadline != 0L) {
            holder.binding.textViewImposedDeadlineInfo.visibility = View.VISIBLE
            holder.binding.textViewImposedDeadline.visibility = View.VISIBLE

            holder.binding.textViewImposedDeadline.text =
                DateFormater.getDateTimeFromMillis(taskWithSubtasks.task.deadline)
        } else {
            holder.binding.textViewImposedDeadline.text = ""

            holder.binding.textViewImposedDeadlineInfo.visibility = View.GONE
            holder.binding.textViewImposedDeadline.visibility = View.GONE
        }

        holder.binding.textViewDescription.text = taskWithSubtasks.task.description

        holder.binding.textViewPriority.text = taskWithSubtasks.task.priority.value

        if (taskWithSubtasks.subtasks.isNotEmpty()) {
            holder.binding.textViewSubtasks.text = holder.itemView.context.getString(
                R.string.task_progress_text,
                taskWithSubtasks.getNumberOfCompletedSubtasks(),
                taskWithSubtasks.getNumberOfSubtasks()
            )
        } else {
            holder.binding.textViewSubtasks.text = ""
        }

        setProgressBar(
            holder.binding.progressBar, taskWithSubtasks.task.progress,
            taskWithSubtasks.task.status, holder.itemView.context
        )

        holder.binding.textViewProgress.text = holder.itemView.context.getString(
            R.string.progress_percentage,
            taskWithSubtasks.task.progress
        )

        holder.binding.textViewDifficulty.text = taskWithSubtasks.task.difficulty.value

        setBackgroundColor(holder.itemView, taskWithSubtasks.task.status)

        holder.binding.imageButtonMoreActions.setOnClickListener {
            handleMoreClicked(holder.itemView.context, taskWithSubtasks.task.taskId, position)
        }

        holder.binding.root.setOnClickListener {
            listener.onTaskClicked(taskWithSubtasks)
        }
    }

    override fun getItemCount() = tasksList.size

    private fun setProgressBar(
        progressBar: ProgressBar,
        progress: Int,
        status: Status,
        context: Context
    ) {
        val color = when (status) {
            Status.NEW -> {
                context.getColor(R.color.blue)
            }
            Status.COMPLETE -> {
                context.getColor(R.color.green)
            }
            Status.IN_PROGRESS -> {
                context.getColor(R.color.blue)
            }
            Status.OVERDUE -> {
                context.getColor(R.color.red)
            }
        }

        progressBar.progressTintList = ColorStateList.valueOf(color)
        progressBar.progress = progress
    }

    private fun setBackgroundColor(view: View, status: Status) {
        when (status) {
            Status.NEW -> {
                view.setBackgroundColor(view.context.getColor(R.color.taskBlueCard))
            }

            Status.COMPLETE -> {
                view.setBackgroundColor(view.context.getColor(R.color.greenLight))
            }

            Status.OVERDUE -> {
                view.setBackgroundColor(view.context.getColor(R.color.redLight))
            }

            else -> {
                view.setBackgroundColor(view.context.getColor(R.color.white))
            }
        }
    }

    private fun handleMoreClicked(context: Context, taskId: Long, position: Int) {
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(R.layout.view_actions_task_dialog)

        dialog.findViewById<TextView>(R.id.text_view_delete)?.setOnClickListener {
            listener.onDeleteClicked(taskId, position)
            dialog.dismiss()
        }

        dialog.show()
    }

    fun setTasksList(tasks: List<TaskWithSubtasks>) {
        tasksList.clear()
        allTasks.clear()
        allTasks.addAll(tasks)
        tasksList.addAll(tasks)
        notifyItemRangeChanged(0, tasksList.size)
    }

    fun notifyItemDeleted(position: Int) {
        tasksList.removeAt(position)
        Log.d("position", position.toString())
        notifyItemRemoved(position)
        notifyItemChanged(position, tasksList.size)
    }

    fun clearList() {
        val size = tasksList.size
        tasksList.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun getTasksList(): ArrayList<TaskWithSubtasks> {
        return allTasks
    }

    fun setFilteredTasks(tasksWithSubtasks: List<TaskWithSubtasks>) {
        tasksList.clear()
        tasksList.addAll(tasksWithSubtasks)
        notifyDataSetChanged()
    }

    class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemTaskBinding.bind(itemView)
    }

    interface TaskClickListener {

        fun onDeleteClicked(taskId: Long, position: Int)

        fun onTaskClicked(taskWithSubtasks: TaskWithSubtasks)
    }
}