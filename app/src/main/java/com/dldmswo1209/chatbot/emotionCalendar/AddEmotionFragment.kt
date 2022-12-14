package com.dldmswo1209.chatbot.emotionCalendar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.CharacteristicType
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.RadarChartData
import com.dldmswo1209.chatbot.databinding.FragmentAddEmotionBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.emotion_calendar.*
import kotlinx.android.synthetic.main.fragment_add_emotion.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddEmotionFragment : Fragment(R.layout.fragment_add_emotion) {
    private lateinit var binding: FragmentAddEmotionBinding
    private lateinit var calendarFragment: EmotionCalendar
    private lateinit var emotionDB: DatabaseReference
    private var presentEmotion = 0
    private var happy = 0
    private var pleasure = 0
    private var sad = 0
    private var depressed = 0
    private var anger = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEmotionBinding.bind(view)
        initView()
        onClickListenerInit()
        binding.emotionRadioGroup.check(R.id.pleasureButton)
        presentEmotion = R.id.pleasureButton // ?????? ????????? ????????? ???????????? ????????? ????????? ???????????? ?????????

        binding.emotionRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.pleasureButton ->{
                    savePreviousEmotion()
                    updateSeekbar()
                }
                R.id.happyButton -> {
                    savePreviousEmotion()
                    updateSeekbar()
                }
                R.id.sadButton -> {
                    savePreviousEmotion()
                    updateSeekbar()
                }
                R.id.depressedButton ->{
                    savePreviousEmotion()
                    updateSeekbar()
                }
                R.id.angerButton -> {
                    savePreviousEmotion()
                    updateSeekbar()
                }
            }
        }

    }
    private fun initView(){
        // ??? ?????????
        val clickedDate = (activity as MainActivity).date // ???????????? ????????? ????????? ?????????
        // ???????????? ?????? ??????????????? ????????? ????????? ???????????? ??????, ?????? ????????? "" ??? ???????????? ??????
        val userName = (activity as MainActivity).userName

        calendarFragment = EmotionCalendar()

        // ?????? ????????? ??????(????????? ???????????? ??????)
        if(clickedDate == "" || clickedDate == LocalDate.now().toString()) {
            val current = LocalDate.now()
            val formattedDate = current.format(DateTimeFormatter.ofPattern("yyyy??? MM??? dd???"))
            val date = LocalDate.now().toString()
            val yearMonthDay = date.split("-")
            val yearMonth = "${yearMonthDay[0]}-${yearMonthDay[1]}"

            binding.todayDate.text = formattedDate
            emotionDB = Firebase.database.reference.child(userName).child("emotionRecord").child(yearMonth).child(LocalDate.now().toString())
            binding.checkButton.isVisible = true
            binding.addEmotionTitle.text = "????????? ?????? ????????????"
            binding.todayEmotionEditTextView.isEnabled = true
            binding.todayEmotionSubTitle.text = "????????? ?????? ????????????"
            binding.todayEmotionThirdTitle.text = "????????? ??????"
            binding.emotionSeekBar.isEnabled = true

        }else{ // ?????? ?????? ???????????? ????????? ??????
            val dateList = clickedDate.split("-")
            val yearMonthDay = clickedDate.split("-")
            val yearMonth = "${yearMonthDay[0]}-${yearMonthDay[1]}"

            binding.todayDate.text = "${dateList[0]}??? ${dateList[1]}??? ${dateList[2]}???"
            (activity as MainActivity).date=""
            emotionDB = Firebase.database.reference.child(userName).child("emotionRecord").child(yearMonth).child(clickedDate)
            binding.checkButton.isVisible = false
            binding.addEmotionTitle.text = ""
            binding.todayEmotionEditTextView.isEnabled = false
            binding.todayEmotionSubTitle.text = "?????? ?????????"
            binding.todayEmotionThirdTitle.text = "????????? ??????"
            binding.emotionSeekBar.isEnabled = false

        }
        CoroutineScope(Dispatchers.IO).launch {
            async {
                emotionDB.get().addOnCompleteListener {
                    val emotionData = it.result.getValue(EmotionData::class.java)

                    if(emotionData == null){
                        binding.todayEmotionEditTextView.text.clear()
                        happy = 0
                        pleasure = 0
                        sad = 0
                        depressed = 0
                        anger = 0

                        emotionSeekBar.progress = pleasure
                        binding.emotionRadioGroup.check(R.id.pleasureButton)
                        presentEmotion = R.id.pleasureButton // ?????? ????????? ????????? ???????????? ????????? ????????? ???????????? ?????????

                        val dataList = ArrayList<RadarChartData>()
                        dataList.add(RadarChartData(CharacteristicType.happy, happy))
                        dataList.add(RadarChartData(CharacteristicType.sad, sad))
                        dataList.add(RadarChartData(CharacteristicType.anger, anger))
                        dataList.add(RadarChartData(CharacteristicType.depressed, depressed))
                        dataList.add(RadarChartData(CharacteristicType.pleasure, pleasure))

                        binding.emotionRadarChart.setDataList(dataList)
                    }else {

                        binding.apply {
                            // ?????? ????????? ????????? ????????? ???????????? ????????????
                            todayEmotionEditTextView.setText(emotionData.text)
                            happy = emotionData.happy
                            pleasure = emotionData.pleasure
                            sad = emotionData.sad
                            depressed = emotionData.depressed
                            anger = emotionData.anger

                            emotionSeekBar.progress = pleasure
                            binding.emotionRadioGroup.check(R.id.pleasureButton)
                            presentEmotion =
                                R.id.pleasureButton // ?????? ????????? ????????? ???????????? ????????? ????????? ???????????? ?????????

                            val dataList = ArrayList<RadarChartData>()
                            dataList.add(RadarChartData(CharacteristicType.happy, happy))
                            dataList.add(RadarChartData(CharacteristicType.sad, sad))
                            dataList.add(RadarChartData(CharacteristicType.anger, anger))
                            dataList.add(RadarChartData(CharacteristicType.depressed,
                                depressed))
                            dataList.add(RadarChartData(CharacteristicType.pleasure, pleasure))

                            binding.emotionRadarChart.setDataList(dataList)
                        }
                    }
                }.addOnCanceledListener {

                }
            }
        }


    }
    private fun savePreviousEmotion(){
        // ?????? ????????? ?????? ????????? ????????? ???????????? presentEmotion ??? ???????????? ?????? ??????
        // ?????? ?????? ????????? ?????? ????????????, ?????? ????????? ?????? ??????????????? ???????????????.
        // 1. ?????? seekbar.progress ??? ???????????? ?????? ????????? ??????
        // 2. presentEmotion ??? ?????? ?????? ???????????? ??????(?????????)
        // 3 ~ 4 ?????? updateSeekbar() ?????? ??????
        // 3. ?????? ?????? ????????? ?????? 0??? ????????????, seekbar.progress = ?????? ?????? ???
        // 4. ?????? ?????? ?????? 0?????? seekbar.progress = 0

        // ?????? ??????????????? ?????? savePreviousEmotion ????????? ?????? ???, Emotion ????????? ????????????, DB ??? ????????????.
        when(presentEmotion){
            R.id.pleasureButton ->{
                pleasure = binding.emotionSeekBar.progress
            }
            R.id.happyButton ->{
                happy = binding.emotionSeekBar.progress
            }
            R.id.sadButton ->{
                sad = binding.emotionSeekBar.progress
            }
            R.id.depressedButton->{
                depressed = binding.emotionSeekBar.progress
            }
            R.id.angerButton->{
                anger = binding.emotionSeekBar.progress
            }
        }
        presentEmotion = binding.emotionRadioGroup.checkedRadioButtonId
    }
    private fun updateSeekbar(){
        when(presentEmotion){
            R.id.pleasureButton ->{
                binding.emotionSeekBar.progress = pleasure
            }
            R.id.happyButton ->{
                binding.emotionSeekBar.progress = happy
            }
            R.id.sadButton->{
                binding.emotionSeekBar.progress = sad
            }
            R.id.depressedButton->{
                binding.emotionSeekBar.progress = depressed
            }
            R.id.angerButton->{
                binding.emotionSeekBar.progress = anger
            }
        }
    }


    private fun onClickListenerInit() {
        binding.backButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(calendarFragment)
        }
        binding.checkButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(calendarFragment)
        }
        binding.emotionSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar, progress: Int, p2: Boolean) {
                binding.emotionValueTextView.text = "$progress%"

                when(binding.emotionRadioGroup.checkedRadioButtonId) {
                    R.id.happyButton -> {
                        happy = progress
                        updateGraph()
                    }
                    R.id.sadButton -> {
                        sad = progress
                        updateGraph()
                    }
                    R.id.angerButton -> {
                        anger = progress
                        updateGraph()
                    }
                    R.id.depressedButton -> {
                        depressed = progress
                        updateGraph()
                    }
                    R.id.pleasureButton -> {
                        pleasure = progress
                        updateGraph()

                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.checkButton.setOnClickListener {
            // ?????? ??????
            savePreviousEmotion()

            // ?????? ?????? ????????? ?????? ??????
            val text = binding.todayEmotionEditTextView.text.toString()
//            // ????????? ??????
            val emotionData = EmotionData(LocalDate.now().toString(),text, happy, pleasure, sad, depressed, anger)
            emotionDB.setValue(emotionData)

            // ?????? ??????
            (activity as MainActivity).replaceFragment((activity as MainActivity).calendarFragment)
        }
    }
    private fun updateGraph(){
        val dataList = ArrayList<RadarChartData>()
        dataList.add(RadarChartData(CharacteristicType.happy, happy))
        dataList.add(RadarChartData(CharacteristicType.sad, sad))
        dataList.add(RadarChartData(CharacteristicType.anger, anger))
        dataList.add(RadarChartData(CharacteristicType.depressed, depressed))
        dataList.add(RadarChartData(CharacteristicType.pleasure, pleasure))

        binding.emotionRadarChart.setDataList(dataList)
    }

}