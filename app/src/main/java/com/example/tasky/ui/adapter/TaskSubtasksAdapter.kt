package com.example.tasky.ui.adapter

import android.content.res.ColorStateList
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasky.R
import com.example.tasky.data.model.entities.Subtask
import com.example.tasky.data.model.entities.Task
import com.example.tasky.data.model.entities.TaskWithSubtasks
import com.example.tasky.data.model.enums.SubtaskStatus
import com.example.tasky.databinding.ItemSubtaskBinding
import com.example.tasky.databinding.ItemTaskMoreBinding
import com.example.tasky.utils.DateFormater

class TaskSubtasksAdapter(
    private val listener: Listener
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
            initTaskWithSubtasks(holder)
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

    private fun initTaskWithSubtasks(holder: TaskViewHolder) {

    }

    private fun initTimeTracking(holder: TaskViewHolder) {
        holder.chronometer.base = SystemClock.elapsedRealtime() - task.spentTime
        holder.buttonTime.setOnClickListener {
            // start time tracking
            if (!isTracking) {
                holder.buttonTime.setImageResource(R.drawable.ic_baseline_pause_24)
                if (lastPause == 0L) {
                    holder.chronometer.base = SystemClock.elapsedRealtime() - task.spentTime
                }
                else { // resume
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
        val subtask = subtasks[position]

        holder.textViewNumber.text = position.toString()
        holder.textViewTitle.text = subtask.title
        holder.textViewDescription.text = subtask.description

        initButtons(holder, subtask)
        initButtonsListeners(holder, subtask)
    }

    private fun initButtons(holder: SubtasksViewHolder, subtask: Subtask) {
        when (subtask.status) {
            SubtaskStatus.NEW -> {
                holder.buttonNew.isEnabled = false
                holder.buttonInProgress.isEnabled = true
                holder.buttonComplete.isEnabled = false

                holder.buttonNew.backgroundTintList = ColorStateList.valueOf(
                    holder.itemView.context.getColor(R.color.generalBlueColor)
                )
                holder.buttonInProgress.backgroundTintList = ColorStateList.valueOf(
                    holder.itemView.context.getColor(R.color.grey)
                )
                holder.buttonComplete.backgroundTintList = ColorStateList.valueOf(
                    holder.itemView.context.getColor(R.color.grey)
                )
            }

            SubtaskStatus.IN_PROGRESS -> {
                holder.buttonNew.isEnabled = false
                holder.buttonInProgress.isEnabled = false
                holder.buttonComplete.isEnabled = true

                holder.buttonNew.backgroundTintList = ColorStateList.valueOf(
                    holder.itemView.context.getColor(R.color.grey)
                )
                holder.buttonInProgress.backgroundTintList = ColorStateList.valueOf(
                    holder.itemView.context.getColor(R.color.generalBlueColor)
                )
                holder.buttonComplete.backgroundTintList = ColorStateList.valueOf(
                    holder.itemView.context.getColor(R.color.grey)
                )
            }

            SubtaskStatus.COMPLETE -> {
                holder.buttonNew.isEnabled = false
                holder.buttonInProgress.isEnabled = true
                holder.buttonComplete.isEnabled = false

                holder.buttonNew.backgroundTintList = ColorStateList.valueOf(
                    holder.itemView.context.getColor(R.color.grey)
                )
                holder.buttonInProgress.backgroundTintList = ColorStateList.valueOf(
                    holder.itemView.context.getColor(R.color.grey)
                )
                holder.buttonComplete.backgroundTintList = ColorStateList.valueOf(
                    holder.itemView.context.getColor(R.color.green)
                )
            }
        }
    }

    private fun initButtonsListeners(holder: SubtasksViewHolder, subtask: Subtask) {
        holder.buttonInProgress.setOnClickListener {
            subtask.status = SubtaskStatus.IN_PROGRESS
            listener.onSubtaskStatusChange(subtask)
            initButtons(holder, subtask)
        }

        holder.buttonComplete.setOnClickListener {
            subtask.status = SubtaskStatus.COMPLETE
            listener.onSubtaskStatusChange(subtask)
            initButtons(holder, subtask)
        }
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
        val textViewTitle = binding.textViewTitle
        val textViewDescription = binding.textViewDescription
        val buttonNew = binding.buttonNew
        val buttonInProgress = binding.buttonInProgress
        val buttonComplete = binding.buttonComplete
    }

    companion object {
        const val VIEW_TYPE_TASK = 1
        const val VIEW_TYPE_SUBTASK = 2
    }

    interface Listener {

        fun onTaskProgressUpdated(taskId: Long, progress: Int)

        fun onStopTimeClicked(taskId: Long, spentTime: Long)

        fun onSubtaskStatusChange(subtask: Subtask)
    }
}