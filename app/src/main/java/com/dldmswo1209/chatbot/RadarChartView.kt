package com.dldmswo1209.chatbot

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.github.mikephil.charting.utils.ColorTemplate
import java.lang.Math.*
import kotlin.math.cos
import kotlin.math.sin

enum class CharacteristicType(val value: String) {
    happy("행복"),
    sad("슬픔"),
    anger("분노"),
    depressed("우울"),
    pleasure("기쁨")
}

data class RadarChartData(
    val type: CharacteristicType,
    val value: Int
)

class RadarChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var dataList: ArrayList<RadarChartData>? = null


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
    private var path = Path()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        paint.color = ColorTemplate.rgb("#7AB0B7")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f


        val radian = PI.toFloat() * 2 / 5 // 360도를 5분할한 각만큼 회전시키 위해
        val step = 5 // 데이터 가이드 라인은 5단계로 표시한다
        val heightMaxValue = height / 2 * 0.7f // RadarChartView영역내에 모든 그림이 그려지도록 max value가 그려질 높이
        val heightStep = heightMaxValue / step // 1단계에 해당하는 높이
        val cx = width / 2f
        val cy = height / 2f
        // 1. 단계별 가이드라인(5각형) 그리기
        for (i in 0..step) {
            var startX = cx
            var startY = (cy - heightMaxValue) + heightStep * i
            repeat(chartTypes.size) {
                // 중심좌표를 기준으로 점(startX,startY)를 radian만큼씩 회전시킨 점(stopX, stopY)을 계산한다.
                val stopPoint = transformRotate(radian, startX, startY, cx, cy)
                canvas.drawLine(startX, startY, stopPoint.x, stopPoint.y, paint)

                startX = stopPoint.x
                startY = stopPoint.y
            }
        }

        // 3. 각 꼭지점 부근에 각 특성 문자열 표시하기
        var startX = cx
        var startY = cy - heightMaxValue
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 40f

        startX = cx
        startY = (cy - heightMaxValue) * 0.7f
        var r = 0f
        path.reset()
        chartTypes.forEach { type ->
            val point = transformRotate(r, startX, startY, cx, cy)
            canvas.drawText(
                type.value,
                point.x,
                textPaint.fontMetrics.getBaseLine(point.y),
                textPaint
            )

            // 전달된 데이터를 표시하는 path 계산
            dataList?.firstOrNull { it.type == type }?.value?.let { value ->
                val conValue = heightMaxValue * value / 100 // 차트크기에 맞게 변환
                val valuePoint = transformRotate(r, startX, cy - conValue, cx, cy)
                if (path.isEmpty) {
                    path.moveTo(valuePoint.x, valuePoint.y)
                } else {
                    path.lineTo(valuePoint.x, valuePoint.y)
                }
            }

            r += radian
        }

        // 4. 전달된 데이터 표시
        path.close()
        paint.color = ColorTemplate.colorWithAlpha(ColorTemplate.rgb("#7AB0B7"),90)
        paint.style = Paint.Style.FILL
        canvas.drawPath(path, paint)

    }

    fun setDataList(dataList: ArrayList<RadarChartData>) {
        if (dataList.isEmpty()) {
            return
        }
        this.dataList = dataList
        invalidate()
    }

    // 점(x, y)를 특정 좌표(cx, cy)를 중심으로 radian만큼 회전시킨 점의 좌표를 반환
    private fun transformRotate(radian: Float, x: Float, y: Float, cx: Float, cy: Float): PointF {
        val stopX = cos(radian) * (x - cx) - sin(radian) * (y - cy) + cx
        val stopY = sin(radian) * (x - cx) + cos(radian) * (y - cy) + cy

        return PointF(stopX, stopY)
    }
}

// y좌표가 중심이 오도록 문자열을 그릴수 있도록하는 baseline좌표를 반환
fun Paint.FontMetrics.getBaseLine(y: Float): Float {
    val halfTextAreaHeight = (bottom - top) / 2
    return y - halfTextAreaHeight - top
}