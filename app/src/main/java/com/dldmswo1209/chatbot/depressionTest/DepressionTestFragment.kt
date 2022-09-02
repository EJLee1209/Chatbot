package com.dldmswo1209.chatbot.depressionTest

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentDepressionTestBinding

class DepressionTestFragment : Fragment(R.layout.fragment_depression_test) {
    private lateinit var binding: FragmentDepressionTestBinding
    private val testScore = mutableListOf(0,0,0,0,0)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDepressionTestBinding.bind(view)

        binding.backButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).recommendTestFragment)
        }

        binding.questionRadioGroup1.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.first1->{
                    testScore[0] = 0
                }
                R.id.second1->{
                    testScore[0] = 5
                }
                R.id.third1->{
                    testScore[0] = 10
                }
                R.id.fourth1->{
                    testScore[0] = 15
                }
                R.id.fifth1->{
                    testScore[0] = 20
                }
            }
            checkAllAnswer()

        }
        binding.questionRadioGroup2.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.first2->{
                    testScore[1] = 0
                }
                R.id.second2->{
                    testScore[1] = 5
                }
                R.id.third2->{
                    testScore[1] = 10
                }
                R.id.fourth2->{
                    testScore[1] = 15
                }
                R.id.fifth2->{
                    testScore[1] = 20
                }
            }
            checkAllAnswer()

        }
        binding.questionRadioGroup3.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.first3->{
                    testScore[2] = 0
                }
                R.id.second3->{
                    testScore[2] = 5
                }
                R.id.third3->{
                    testScore[2] = 10
                }
                R.id.fourth3->{
                    testScore[2] = 15
                }
                R.id.fifth3->{
                    testScore[2] = 20
                }
            }
            checkAllAnswer()
        }
        binding.questionRadioGroup4.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.first4->{
                    testScore[3] = 0
                }
                R.id.second4->{
                    testScore[3] = 5
                }
                R.id.third4->{
                    testScore[3] = 10
                }
                R.id.fourth4->{
                    testScore[3] = 15
                }
                R.id.fifth4->{
                    testScore[3] = 20
                }
            }
            checkAllAnswer()
        }
        binding.questionRadioGroup5.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.first5->{
                    testScore[4] = 0
                }
                R.id.second5->{
                    testScore[4] = 5
                }
                R.id.third5->{
                    testScore[4] = 10
                }
                R.id.fourth5->{
                    testScore[4] = 15
                }
                R.id.fifth5->{
                    testScore[4] = 20
                }
            }
            checkAllAnswer()

        }
    }

    private fun checkAllAnswer(){
        val q1 = binding.questionRadioGroup1.checkedRadioButtonId
        val q2 = binding.questionRadioGroup2.checkedRadioButtonId
        val q3 = binding.questionRadioGroup3.checkedRadioButtonId
        val q4 = binding.questionRadioGroup4.checkedRadioButtonId
        val q5 = binding.questionRadioGroup5.checkedRadioButtonId

        if(q1 == -1) return
        if(q2 == -1) return
        if(q3 == -1) return
        if(q4 == -1) return
        if(q5 == -1) return

        val score = calcTestScore()

        if(score >= 65) {
            clearCheck()
            (activity as MainActivity).replaceFragment((activity as MainActivity).helpFragment)
        }
        else {
            clearCheck()
            (activity as MainActivity).replaceFragment((activity as MainActivity).recommendTestFragment)
        }

    }
    private fun calcTestScore(): Int{
        var sum = 0
        testScore.forEach {
            sum += it
        }
        return sum
    }
    private fun clearCheck(){
        binding.questionRadioGroup1.clearCheck()
        binding.questionRadioGroup2.clearCheck()
        binding.questionRadioGroup3.clearCheck()
        binding.questionRadioGroup4.clearCheck()
        binding.questionRadioGroup5.clearCheck()
    }


}