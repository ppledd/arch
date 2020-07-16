package com.zjy.arch.fragment

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.zjy.architecture.ext.dp
import kotlinx.android.synthetic.main.fragment_chart.*


/**
 * @author zhengjy
 * @since 2020/07/03
 * Description:
 */
class ChartFragment(layout: Int) : Fragment(layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lc_chart.apply {
            minOffset = 25f
            setTouchEnabled(false)
            // 设置图例
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.setDrawInside(true)
            legend.yOffset = 18f
            legend.setExtra(arrayOf(LegendEntry("单位(D)", Legend.LegendForm.NONE, 10f, 20f,
                DashPathEffect(floatArrayOf(10f, 5f), 0f), Color.parseColor("#000000"))))
            // 设置x轴
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.labelCount = 12
            xAxis.axisMinimum = 1f
            xAxis.axisMaximum = 12f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}月"
                }
            }
            // 设置y轴
            axisLeft.setGridDashedLine(DashPathEffect(floatArrayOf(10f, 5f), 0f))
            axisLeft.setDrawAxisLine(false)
            axisRight.isEnabled = false
            // 设置表格名称
            description = Description().apply {
                text = "会员月视力趋势图"
                textSize = 13f
//                val output = IntArray(2)
//                lc_chart.getLocationInWindow(output)
//                setPosition((output[0] + lc_chart.paddingLeft).toFloat(), (output[1] + lc_chart.paddingTop).toFloat())
                setPosition(115.dp.toFloat(), 20.dp.toFloat())
            }
            // 设置数据
            data = LineData(
                LineDataSet(
                    listOf(
                        Entry(1f, 650f),
                        Entry(2f, 675f),
                        Entry(3f, 650f),
                        Entry(4f, 700f),
                        Entry(5f, 800f),
                        Entry(6f, 700f),
                        Entry(7f, 650f),
                        Entry(8f, -275f),
                        Entry(9f, 800f),
                        Entry(10f, 825f)
                    ), "会员左眼视力"
                ).apply {
                    setDrawCircles(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                },
                LineDataSet(
                    listOf(
                        Entry(1f, 350f),
                        Entry(2f, 275f),
                        Entry(3f, 350f),
                        Entry(4f, -100f),
                        Entry(5f, 275f),
                        Entry(6f, 250f)
                    ), "会员右眼视力"
                ).apply {
                    setDrawCircles(false)
                    setCircleColor(Color.parseColor("#86C649"))
                    color = Color.parseColor("#86C649")
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                },
                LineDataSet(
                    listOf(
                        Entry(1f, 650f),
                        Entry(2f, 175f),
                        Entry(3f, 350f),
                        Entry(4f, 100f),
                        Entry(5f, 575f),
                        Entry(6f, 750f),
                        Entry(7f, 650f),
                        Entry(8f, 570f),
                        Entry(9f, 750f)
                    ), "会员右眼视力"
                ).apply {
                    setDrawCircles(false)
                    setCircleColor(Color.parseColor("#9722FE"))
                    color = Color.parseColor("#9722FE")
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                },
                LineDataSet(
                    listOf(
                        Entry(1f, 250f),
                        Entry(2f, 475f),
                        Entry(3f, 370f),
                        Entry(4f, -25f),
                        Entry(5f, 250f),
                        Entry(6f, 300f),
                        Entry(7f, -100f),
                        Entry(8f, 275f),
                        Entry(9f, 150f),
                        Entry(10f, 575f)
                    ), "会员右眼视力"
                ).apply {
                    setDrawCircles(false)
                    setCircleColor(Color.parseColor("#03C9E6"))
                    color = Color.parseColor("#03C9E6")
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                }
            )
            // 设置动画
            animateXY(400, 400)
        }
    }
}