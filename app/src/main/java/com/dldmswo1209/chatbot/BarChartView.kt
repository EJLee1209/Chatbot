package com.dldmswo1209.chatbot

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import com.github.mikephil.charting.utils.ColorTemplate


data class BarChartData(
    val type: CharacteristicType,
    val value: Int
)

class BarChartView(context: Context?, attrs: AttributeSet): View(context, attrs) {
    private var dataList = ArrayList<BarChartData>()
    private var mPointX = mutableListOf<Int>()
    private var mPointY = mutableListOf<Int>()
    private var mUnit = 0
    private var mOrigin = 0
    private var mDivide = 0

    // 5개의 특성을 갖도록 한다
    private var chartTypes = arrayListOf(
        CharacteristicType.happy,
        CharacteristicType.sad,
        CharacteristicType.anger,
        CharacteristicType.depressed,
        CharacteristicType.pleasure
    )

    private val paint = Paint().apply {
        isAntiAlias = true

    }
    private val textPaint = TextPaint().apply {
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if(dataList.isEmpty()) return

        paint.color = ColorTemplate.rgb("#EACB82")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 60f

        //x축 막대 사이의 거리
        val gapX = width / dataList.size
        //y축 단위 사이의 거리
        val gapY = height / 10
        val halfGab = gapX / 2

        val length = dataList.size

        for(i in 0 until length) {
        //막대 좌표를 구한다
            val x = halfGab + (i*gapX)
            val y = height - (((dataList[i].value/mUnit)-mOrigin)*gapY)

            mPointX.add(x)
            mPointY.add(y)
        }

        if(mPointX != null && mPointY != null) {
            val length = mPointX.size;

            val bottom = height
            for (i in 0 until length) {
                val x = mPointX[i]
                val y = mPointY[i]

                if(dataList[i].type == CharacteristicType.happy || dataList[i].type == CharacteristicType.pleasure){
                    paint.color = ColorTemplate.rgb("#EACB82")
                }
                else{
                    paint.color = ColorTemplate.rgb("#DCE8E4")
                }
                //믹대를 그린다
                textPaint.textAlign = Paint.Align.CENTER
                textPaint.textSize = 40f
                val textYPos = mOrigin+height-5

                canvas!!.drawLine(x.toFloat(), y.toFloat()-70, x.toFloat(), bottom.toFloat()-70, paint)
                canvas!!.drawText(dataList[i].type.value, x.toFloat(), textYPos.toFloat(), textPaint)
            }

    }

    }


    fun setData(dataList: ArrayList<BarChartData>, unit: Int, origin: Int, divide: Int) {
        if (dataList.isEmpty()) {
            return
        }
        this.dataList = dataList
        mUnit = unit
        mOrigin = origin
        mDivide = divide
        invalidate()
    }
}