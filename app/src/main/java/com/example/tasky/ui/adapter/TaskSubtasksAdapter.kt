package com.example.tasky.ui.adapter

import android.os.SystemClock
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
import com.example.tasky.utils.DateFormater

class TaskSubtasksAdapter(
    private val listener: TaskListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var task: Task
    private val subtasks: ArrayList<Subtask> = arrayListOf()

    private var isTracking = false
    private var lastPause: Long = 0

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
        if (subtasks.size == 0) {
            initTask(holder)
        } else {
            initTaskWithSubtasks()
        }
        holder.textViewDomain.text = task.domain
        holder.textViewTitle.text = task.title
        holder.textViewDescription.text = task.description
        holder.textViewDeadline.text = DateFormater.getDateTimeFromMillis(task.deadline)

        if (task.imposedDeadline != 0L) {
            holder.textViewImposedDeadlineInfo.visibility = View.VISIBLE
            holder.textViewImposedDeadline.visibility = View.VISIBLE
            holder.textViewImposedDeadline.text =
                DateFormater.getDateTimeFromMillis(task.imposedDeadline)
        } else {
            holder.textViewImposedDeadlineInfo.visibility = View.GONE
            holder.textViewImposedDeadline.visibility = View.GONE
        }

        holder.textViewPriority.text = task.priority.value
        holder.textViewDifficulty.text = task.difficulty.value
        initTimeTracking(holder)
    }

    private fun initTask(holder: TaskViewHolder) {
        holder.progressBar.visibility = View.VISIBLE
        holder.textViewProgress.visibility = View.VISIBLE

        holder.progressBar.value = task.progress.toFloat()
        holder.textViewProgress.text = holder.itemView.context.getString(
            R.string.progress_percentage,
            task.progress
        )

        holder.progressBar.addOnChangeListener { _, value, _ ->
            holder.textViewProgress.text =
                holder.itemView.context.getString(R.string.progress_percentage, value.toInt())
            listener.onTaskProgressUpdated(task.taskId, value.toInt())
        }
    }

    private fun initTaskWithSubtasks() {

    }

    private fun initTimeTracking(holder: TaskViewHolder) {
        holder.chronometer.base = SystemClock.elapsedRealtime() - task.spentTime
        holder.buttonTime.setOnClickListener {
            // start time tracking
            if (!isTracking) {
                holder.buttonTime.setImageResource(R.drawable.ic_baseline_pause_24)
                if (lastPause != 0L) { // resume
                    holder.chronometer.base = holder.chronometer.base +
                            SystemClock.elapsedRealtime() - lastPause
                }
                holder.chronometer.start()

            } else { // stop time tracking
                holder.buttonTime.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                lastPause = SystemClock.elapsedRealtime()
                holder.chronometer.stop()
                listener.onStopTimeClicked(task.taskId, (holder.chronometer.base - lastPause) * -1)
            }
            isTracking = !isTracking
        }
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
        val progressBar = binding.taskProgressBar
        val textViewProgress = binding.textViewProgress
        val textViewDeadline = binding.textViewDeadline
        val textViewImposedDeadline = binding.textViewImposedDeadline
        val textViewImposedDeadlineInfo = binding.textViewImposedDeadlineInfo
        val textViewPriority = binding.textViewPriority
        val textViewDifficulty = binding.textViewDifficulty
        val chronometer = binding.chronometer
        val buttonTime = binding.buttonTime
    }

    inner class SubtasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSubtaskBinding.bind(itemView)
        val textViewNumber = binding.textViewNumber
    }

    companion object {
        const val VIEW_TYPE_TASK = 1
        const val VIEW_TYPE_SUBTASK = 2
    }

    interface TaskListener {

        fun onTaskProgressUpdated(taskId: Long, progress: Int)

        fun onStopTimeClicked(taskId: Long, spentTime: Long)
    }
}