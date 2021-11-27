package com.example.todo.framework.presentation.view.fragments.homeflow

import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.todo.R
import com.example.todo.business.domain.model.Profile
import com.example.todo.databinding.FragmentProfileBinding
import com.example.todo.framework.presentation.base.BaseFragment
import com.example.todo.framework.presentation.viewmodel.fragments.homeflow.ProfileFragmentViewModel
import com.example.todo.util.Resource
import com.example.todo.util.showSnack
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val mViewModel by viewModels<ProfileFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.meAndMyStats()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        mViewModel.meAndMyStats.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    showProgress(false)
                    showSnack(it.message!!)
                }
                is Resource.Loading -> showProgress(true)
                is Resource.Success -> {
                    showProgress(false)
                    setMyInfo(it.data!!)
                }
            }
        }
    }

    private fun setMyInfo(myProfile: Profile) {
        binding.apply {
            emailTv.text = myProfile.email
            fullNameTv.text = myProfile.fullname
            val entries = mutableListOf<PieEntry>()

            entries.add(PieEntry(100f * myProfile.active / myProfile.total, "Active"))
            entries.add(PieEntry(100f * myProfile.completed / myProfile.total, "Completed"))

            val dataSet = PieDataSet(entries, "")

            dataSet.setColors(
                ColorTemplate.MATERIAL_COLORS[1],
                ColorTemplate.MATERIAL_COLORS[0]
            )

            val pieData = PieData(dataSet)

            pieData.apply {
                setDrawValues(true)
                setValueFormatter(PercentFormatter(binding.pieChart))
                setValueTextSize(12f)
                setValueTextColor(Color.BLACK)
            }


            pieChart.apply {
                isDrawHoleEnabled = true
                setUsePercentValues(true)
                legend.isEnabled = false
                centerText = "Total : ${myProfile.total}"
                description.isEnabled = false
                data = pieData
                animateY(1400, Easing.EaseInQuad)
            }
        }
    }
}