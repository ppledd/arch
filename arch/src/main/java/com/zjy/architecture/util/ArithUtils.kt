package com.zjy.architecture.util

import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.jvm.JvmOverloads

/**
 * @author zhengjy
 * @since 2018/09/19
 * Description: Double运算工具类
 */
object ArithUtils {
    // 默认除法运算精度
    private const val DEF_DIV_SCALE = 10
    private val format = DecimalFormat("#0.##########")

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    fun add(v1: Double, v2: Double): Double {
        val b1 = BigDecimal(format.format(v1))
        val b2 = BigDecimal(format.format(v2))
        return b1.add(b2).toDouble()
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    fun sub(v1: Double, v2: Double): Double {
        val b1 = BigDecimal(format.format(v1))
        val b2 = BigDecimal(format.format(v2))
        return b1.subtract(b2).toDouble()
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    fun mul(v1: Double, v2: Double): Double {
        val b1 = BigDecimal(format.format(v1))
        val b2 = BigDecimal(format.format(v2))
        return b1.multiply(b2).toDouble()
    }
    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    @JvmOverloads
    fun div(v1: Double, v2: Double, scale: Int = DEF_DIV_SCALE): Double {
        require(scale >= 0) { "The scale must be a positive integer or zero" }
        val b1 = BigDecimal(format.format(v1))
        val b2 = BigDecimal(format.format(v2))
        return b1.divide(b2, scale, BigDecimal.ROUND_DOWN).toDouble()
    }

    fun div(v1: Double, v2: Double, scale: Int, roundMode: Int): Double {
        require(scale >= 0) { "The scale must be a positive integer or zero" }
        val b1 = BigDecimal(format.format(v1))
        val b2 = BigDecimal(format.format(v2))
        return b1.divide(b2, scale, roundMode).toDouble()
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    fun round(v: Double, scale: Int): Double {
        require(scale >= 0) { "The scale must be a positive integer or zero" }
        val b = BigDecimal(format.format(v))
        val one = BigDecimal("1")
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toDouble()
    }
}