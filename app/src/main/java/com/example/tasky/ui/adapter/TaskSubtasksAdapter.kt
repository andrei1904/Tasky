package com.example.tasky.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasky.R
import com.example.tasky.data.model.entities.Subtask
import com.example.tasky.data.model.entities.Task
import com.example.tasky.data.model.entities.TaskWithSubtasks
import com.example.tasky.databinding.ItemSubtaskBinding
import com.example.tasky.databinding.ItemTaskMoreBinding

class TaskSubtasksAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var task: Task
    private val subtasks: ArrayList<Subtask> = arrayListOf()

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return VIEW_TYPE_TASK
        }
        return VIEW_TYPE_SUBTASK
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_TASK) {
            return TaskViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_task_more, parent, false)
            )
        }

        return SubtasksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_subtask, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_TASK && holder is TaskViewHolder) {
            bindTask(holder)
        } else {
            bindSubtask(holder as SubtasksViewHolder, position - 1)
        }
    }

    override fun getItemCount(): Int = subtasks.size + 1

    private fun bindTask(holder: TaskViewHolder) {
        holder.textViewDomain.text = task.domain
        holder.textViewTitle.text = task.title
        holder.textViewDescription.text = task.description
    }

    private fun bindSubtask(holder: SubtasksViewHolder, position: Int) {
        holder.textViewNumber.text = position.toString()
    }

    fun setData(taskWithSubtasks: TaskWithSubtasks) {
        task = taskWithSubtasks.task
        subtasks.clear()
        subtasks.addAll(taskWithSubtasks.subtasks)
        notifyItemRangeChanged(0, subtasks.size + 1)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTaskMoreBinding.bind(itemView)
        val textViewTitle = binding.textViewTitle
        val textViewDomain = binding.textViewDomain
        val textViewDescription = binding.textViewDescription

    }

    inner class SubtasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSubtaskBinding.bind(itemView)
        val textViewNumber = binding.textViewNumber
    }

    companion object {
        const val VIEW_TYPE_TASK = 1
        const val VIEW_TYPE_SUBTASK = 2
    }
}