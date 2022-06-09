package com.example.tasky.ui.adapter

import android.content.res.ColorStateList
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.baoyachi.stepview.bean.StepBean
import com.example.tasky.R
import com.example.tasky.data.model.entities.Subtask
import com.example.tasky.data.model.entities.Task
import com.example.tasky.data.model.entities.TaskWithSubtasks
import com.example.tasky.data.model.enums.Difficulty
import com.example.tasky.data.model.enums.ProgressBarType
import com.example.tasky.data.model.enums.SubtaskStatus
import com.example.tasky.databinding.ItemSubtaskBinding
import com.example.tasky.databinding.ItemTaskMoreBinding
import com.example.tasky.utils.DateFormater

class TaskSubtasksAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var task: Task
    private val subtasks: ArrayList<Subtask> = arrayListOf()
    private var currentSubtask: Int = 0

    private var isTracking = false
    private var lastPause: Long = 0
    private val percentage = mutableMapOf<Int, Double>()

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

        holder.textViewProgress.visibility = View.VISIBLE
        holder.textViewProgress.text = task.progress.toString()

        when (task.progressBarType) {
            ProgressBarType.TYPE0 -> {
                initTaskType0(holder)
            }
            ProgressBarType.TYPE1 -> {
                initTaskType1(holder)
                splitDifficultyType1()
            }
            ProgressBarType.TYPE2 -> {
                initTaskType2(holder)
                splitDifficultyType1()
            }
        }
    }

    private fun splitDifficultyType1() {
        val result = 100.0 / subtasks.size
        percentage[0] = result
        percentage[1] = result
        percentage[2] = result
    }

    private fun initTaskType0(holder: TaskViewHolder) {
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

    private fun initTaskType1(holder: TaskViewHolder) {
        holder.progressBarSteps.visibility = View.VISIBLE

        holder.textViewProgress.text = holder.itemView.context.getString(
            R.string.progress_percentage,
            task.progress
        )

        initProgressSteps(holder)
    }

    private fun initTaskType2(holder: TaskViewHolder) {
        holder.progressBarSteps.visibility = View.VISIBLE

        initProgressLine(holder)
    }

    private fun initProgressSteps(holder: TaskViewHolder) {
        val stepBeans = ArrayList<StepBean>()
        for ((position, subtask) in subtasks.withIndex()) {
            val title: Int = position + 1
            when (subtask.status) {
                SubtaskStatus.NEW -> {
                    stepBeans.add(StepBean(title.toString(), -1))
                }
                SubtaskStatus.IN_PROGRESS -> {
                    stepBeans.add(StepBean(title.toString(), 0))
                }
                SubtaskStatus.COMPLETE -> {
                    stepBeans.add(StepBean(title.toString(), 1))
                }
            }
        }

        holder.progressBarSteps
            .setStepViewTexts(stepBeans)
            .setTextSize(12)
            .setStepViewUnComplectedTextColor(holder.itemView.context.getColor(R.color.black))
            .setStepViewComplectedTextColor(holder.itemView.context.getColor(R.color.black))
            .setStepsViewIndicatorUnCompletedLineColor(holder.itemView.context.getColor(R.color.black))
            .setStepsViewIndicatorCompletedLineColor(holder.itemView.context.getColor(R.color.black))
            .setStepsViewIndicatorCompleteIcon(
                AppCompatResources.getDrawable(holder.itemView.context, R.drawable.circle_done)
            )
            .setStepsViewIndicatorAttentionIcon(
                AppCompatResources.getDrawable(
                    holder.itemView.context,
                    R.drawable.circle_in_progress
                )
            )
            .setStepsViewIndicatorDefaultIcon(
                AppCompatResources.getDrawable(
                    holder.itemView.context,
                    R.drawable.circle
                )
            )
    }

    private fun initProgressLine(holder: TaskViewHolder) {
        holder.progressBarLine.visibility = View.VISIBLE

        holder.progressBarLine.progress = task.progress
        holder.progressBarLine.progressTintList = ColorStateList.valueOf(
            holder.itemView.context.getColor(R.color.blue)
        )
    }

    private fun initTimeTracking(holder: TaskViewHolder) {
        holder.chronometer.base = SystemClock.elapsedRealtime() - task.spentTime
        holder.buttonTime.setOnClickListener {
            // start time tracking
            if (!isTracking) {
                holder.buttonTime.setImageResource(R.drawable.ic_baseline_pause_24)
                if (lastPause == 0L) {
                    holder.chronometer.base = SystemClock.elapsedRealtime() - task.spentTime
                } else { // resume
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

        val number: Int = position + 1
        holder.textViewNumber.text = number.toString()
        holder.textViewTitle.text = subtask.title
        holder.textViewDescription.text = subtask.description
        holder.textViewDifficulty.text = subtask.difficulty.value

        initButtons(holder, subtask, position)
        initButtonsListeners(holder, subtask, position)
    }

    private fun initButtons(holder: SubtasksViewHolder, subtask: Subtask, position: Int) {
        when (subtask.status) {
            SubtaskStatus.NEW -> {
                if (position == currentSubtask) {
                    holder.buttonNew.isEnabled = false
                    holder.buttonInProgress.isEnabled = true
                    holder.buttonComplete.isEnabled = false
                } else {
                    holder.buttonNew.isEnabled = false
                    holder.buttonInProgress.isEnabled = false
                    holder.buttonComplete.isEnabled = false
                }

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
                if (position == currentSubtask) {
                    holder.buttonNew.isEnabled = false
                    holder.buttonInProgress.isEnabled = false
                    holder.buttonComplete.isEnabled = true
                } else {
                    holder.buttonNew.isEnabled = false
                    holder.buttonInProgress.isEnabled = false
                    holder.buttonComplete.isEnabled = false
                }

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
                if (position == currentSubtask) {
                    holder.buttonNew.isEnabled = false
                    holder.buttonInProgress.isEnabled = false
                    holder.buttonComplete.isEnabled = false
                } else {
                    holder.buttonNew.isEnabled = false
                    holder.buttonInProgress.isEnabled = false
                    holder.buttonComplete.isEnabled = false
                }

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

    private fun initButtonsListeners(holder: SubtasksViewHolder, subtask: Subtask, position: Int) {
        holder.buttonInProgress.setOnClickListener {
            subtask.status = SubtaskStatus.IN_PROGRESS
            listener.onSubtaskStatusChange(subtask)
            initButtons(holder, subtask, position)
            notifyItemChanged(0)
        }

        holder.buttonComplete.setOnClickListener {
            subtask.status = SubtaskStatus.COMPLETE
            listener.onSubtaskStatusChange(subtask)
            updatePercentage(subtask)
            if (currentSubtask < subtasks.size - 1) {
                currentSubtask++
                notifyItemChanged(currentSubtask + 1)
            }

            initButtons(holder, subtask, position)
            notifyItemChanged(0)
        }
    }

    private fun updatePercentage(subtask: Subtask) {
        when (subtask.difficulty) {
            Difficulty.EASY -> {
                task.progress = task.progress + (percentage[0]?.toInt() ?: 0)
            }
            Difficulty.MEDIUM -> {
                task.progress = task.progress + (percentage[1]?.toInt() ?: 0)
            }
            Difficulty.HARD -> {
                task.progress = task.progress + (percentage[2]?.toInt() ?: 0)
            }
        }
        if (task.progress >= 95) {
            task.progress = 100
        }
        listener.onTaskProgressUpdated(task.taskId, task.progress)
    }

    fun setData(taskWithSubtasks: TaskWithSubtasks) {
        task = taskWithSubtasks.task
        subtasks.clear()
        subtasks.addAll(taskWithSubtasks.subtasks)
        notifyItemRangeChanged(0, subtasks.size + 1)
        setCurrentSubtask()
    }

    private fun setCurrentSubtask() {
        for ((position, subtask) in subtasks.withIndex()) {
            if (subtask.status == SubtaskStatus.COMPLETE) {
                continue
            }
            if (subtask.status == SubtaskStatus.IN_PROGRESS) {
                currentSubtask = position
                break
            }
            if (subtask.status == SubtaskStatus.NEW) {
                currentSubtask = position
                break
            }
        }
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTaskMoreBinding.bind(itemView)
        val textViewTitle = binding.textViewTitle
        val textViewDomain = binding.textViewDomain
        val textViewDescription = binding.textViewDescription
        val progressBar = binding.taskProgressBarNoSubtasks
        val textViewProgress = binding.textViewProgress
        val textViewDeadline = binding.textViewDeadline
        val textViewImposedDeadline = binding.textViewImposedDeadline
        val textViewImposedDeadlineInfo = binding.textViewImposedDeadlineInfo
        val textViewPriority = binding.textViewPriority
        val textViewDifficulty = binding.textViewDifficulty
        val chronometer = binding.chronometer
        val buttonTime = binding.buttonTime
        val progressBarSteps = binding.progressBarSteps
        val progressBarLine = binding.progressBarLine

    }

    inner class SubtasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSubtaskBinding.bind(itemView)
        val textViewNumber = binding.textViewNumber
        val textViewTitle = binding.textViewTitle
        val textViewDescription = binding.textViewDescription
        val buttonNew = binding.buttonNew
        val buttonInProgress = binding.buttonInProgress
        val buttonComplete = binding.buttonComplete
        val textViewDifficulty = binding.textViewDifficulty
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