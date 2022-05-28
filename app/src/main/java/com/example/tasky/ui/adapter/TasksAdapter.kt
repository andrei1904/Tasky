package com.example.tasky.ui.adapter

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tasky.R
import com.example.tasky.data.model.TaskWithSubtasks
import com.example.tasky.databinding.ItemTaskBinding
import com.example.tasky.utils.DateFormater
import com.google.android.material.bottomsheet.BottomSheetDialog

class TasksAdapter(
    private val listener: TaskClickListener
) : RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {

    private var tasksList = ArrayList<TaskWithSubtasks>()

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
            holder.binding.textViewImposedDeadline.text =
                DateFormater.getDateTimeFromMillis(taskWithSubtasks.task.deadline)
        }

        holder.binding.textViewDescription.text = taskWithSubtasks.task.description

        holder.binding.textViewPriority.text = taskWithSubtasks.task.priority.value

        holder.binding.textViewProgress.text = holder.itemView.context.getString(
            R.string.task_progress_text,
            taskWithSubtasks.getNumberOfCompletedSubtasks(),
            taskWithSubtasks.getNumberOfSubtasks()
        )

        holder.binding.progressBar.progress = 50

        holder.binding.textViewSubtasks.text = taskWithSubtasks.subtasks.size.toString()

        holder.binding.imageButtonMoreActions.setOnClickListener {
            handleMoreClicked(holder.itemView.context, taskWithSubtasks.task.taskId, position)
        }

        holder.binding.root.setOnClickListener {
            listener.onTaskClicked(taskWithSubtasks)
        }
    }

    override fun getItemCount() = tasksList.size

    private fun handleMoreClicked(context: Context, taskId: Long, position: Int) {
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(R.layout.view_actions_task_dialog)

        dialog.findViewById<TextView>(R.id.text_view_delete)?.setOnClickListener {
            listener.onDeleteClicked(taskId, position)

            Log.d("TASK TITLE + POSITion", "$taskId  $position")
            dialog.dismiss()
        }

        dialog.findViewById<TextView>(R.id.text_view_edit)?.setOnClickListener {
            // EDIT TASK
        }

        dialog.show()
    }

    fun appendToTasksList(tasks: List<TaskWithSubtasks>) {
        tasksList.addAll(tasks)
    }

    fun setTasksList(tasks: List<TaskWithSubtasks>) {
        tasksList.clear()
        tasksList.addAll(tasks)
        notifyItemRangeChanged(0, tasksList.size)
    }

    fun notifyItemDeleted(position: Int) {
        tasksList.removeAt(position)
        Log.d("position", position.toString());
        notifyItemRemoved(position)
        notifyItemChanged(position, tasksList.size)
    }

    class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemTaskBinding.bind(itemView)
    }

    interface TaskClickListener {

        fun onDeleteClicked(taskId: Long, position: Int)

        fun onTaskClicked(taskWithSubtasks: TaskWithSubtasks)
    }
}