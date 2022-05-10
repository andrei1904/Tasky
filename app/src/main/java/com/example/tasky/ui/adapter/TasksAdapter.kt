package com.example.tasky.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tasky.R
import com.example.tasky.data.model.Task
import com.example.tasky.databinding.ItemTaskBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.coroutines.coroutineContext

class TasksAdapter(
    private val listener: DeleteTaskClickListener
) : RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {

    private var tasksList = ArrayList<Task>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TasksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        )

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val task = tasksList[position]

        holder.binding.textViewTaskDomain.text = task.domain

        holder.binding.textViewTaskTitle.text = task.title

        holder.binding.imageButtonMoreActions.setOnClickListener {
            handleMoreClicked(holder.itemView.context, task.taskId, position)
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

    fun appendToTasksList(tasks: List<Task>) {
        tasksList.addAll(tasks)
    }

    fun setTasksList(tasks: List<Task>) {
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

    interface DeleteTaskClickListener {

        fun onDeleteClicked(taskId: Long, position: Int)
    }
}