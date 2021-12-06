package com.example.todo.framework.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.business.domain.model.Stat
import com.example.todo.databinding.StatisticItemBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class StatisticsAdapter : ListAdapter<Stat, StatisticsAdapter.ViewHolder>(STAT_COMPARATOR) {

    companion object {
        val STAT_COMPARATOR = object : DiffUtil.ItemCallback<Stat>() {
            override fun areItemsTheSame(oldItem: Stat, newItem: Stat): Boolean {
                return oldItem.total != newItem.total
            }

            override fun areContentsTheSame(oldItem: Stat, newItem: Stat): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(private val binding: StatisticItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stat: Stat) {
            binding.apply {
                importantTv.text = stat.important.toString()
                notImportantTv.text = stat.notImportant.toString()
                completedTv.text = stat.completed.toString()
                activeTv.text = stat.active.toString()
                val entries = mutableListOf<PieEntry>()

                entries.add(
                    PieEntry(
                        100f * stat.completed / stat.total,
                        "Completed"
                    )
                )
                entries.add(
                    PieEntry(
                        100f * stat.active / stat.total,
                        "Active"
                    )
                )
                entries.add(
                    PieEntry(
                        100f * stat.important / stat.total,
                        "Important"
                    )
                )
                entries.add(
                    PieEntry(
                        100f * stat.notImportant / stat.total,
                        "Not Important"
                    )
                )

                val dataSet = PieDataSet(entries, "")

                dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

                val theData = PieData(dataSet)

                theData.apply {
                    setDrawValues(true)
                    setValueFormatter(PercentFormatter(binding.pieChart))
                    setValueTextSize(12f)
                    setValueTextColor(ContextCompat.getColor(binding.root.context,R.color.text_color))
                }

                pieChart.apply {
                    isDrawHoleEnabled = true
                    setUsePercentValues(true)
                    setEntryLabelTextSize(8f)
                    setEntryLabelColor(ContextCompat.getColor(binding.root.context,R.color.text_color))
                    setCenterTextColor(ContextCompat.getColor(binding.root.context,R.color.text_color))
                    setHoleColor(ContextCompat.getColor(binding.root.context,R.color.card_bg))
                    description.isEnabled = false
                    centerText = "${stat.category}\n Total:${stat.total}"
                    setCenterTextSize(11f)
                    val legend = legend
                    legend.apply {
                        verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                        horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                        legend.orientation = Legend.LegendOrientation.VERTICAL
                        textSize = 12f
                        isEnabled = true
                        textColor = ContextCompat.getColor(binding.root.context,R.color.text_color)
                        form = Legend.LegendForm.CIRCLE
                        yEntrySpace = 10f
                    }
                    invalidate()
                    data = theData

                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            StatisticItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(it)
        }
    }

}