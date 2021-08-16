package com.zjy.architecture.util

import java.lang.Exception
import java.lang.StringBuilder
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.regex.Pattern

/**
 * @author zhengjy
 * @since 2018/09/19
 * Description: 用于金融数据的格式化
 * 如果要进行精确的Double计算则使用[ArithUtils]
 */
object FinanceUtils {
    /**
     * 将Double转换成String，防止出现科学计数法
     *
     * @param num
     * @return
     */
    fun getPlainNum(num: Double, count: Int): String {
        return getMoney(num, false, count)
    }

    fun getPlainNum(num: String?, count: Int): String {
        if (num == null) {
            return ""
        }
        if (num.isNotEmpty() && matchDecimal(num)) {
            return num
        }
        val n = num.replace(",", "")
        return getMoney(n.toDouble(), false, count)
    }

    fun getAccuracy(count: Int): String {
        if (count == 0) {
            return "1"
        }
        val sb = StringBuilder("0.")
        for (i in 0 until count) {
            if (i == count - 1) {
                sb.append("1")
            } else {
                sb.append("0")
            }
        }
        return sb.toString()
    }

    /**
     * 将Double转换成String，防止出现科学计数法，并且格式为: 23,345.98
     *
     * @param num
     * @return
     */
    fun getGroupNum(num: Double, count: Int): String {
        return getMoney(num, true, count)
    }

    fun getGroupNum(num: String?, count: Int): String {
        if (num == null) {
            return ""
        }
        if (num.isNotEmpty() && matchDecimal(num)) {
            return num
        }
        val n = num.replace(",", "")
        return getMoney(n.toDouble(), true, count)
    }

    private fun getMoney(num: Double, group: Boolean, count: Int): String {
        val nf = NumberFormat.getInstance()
        // 整数部分
        nf.maximumIntegerDigits = 100
        // 小数部分
        nf.maximumFractionDigits = 100
        nf.isGroupingUsed = group
        val result = nf.format(num)
        return checkString(result, count)
    }

    /**
     * 去除末位多余0
     * @param num
     * @return
     */
    fun stripZero(num: String?): String {
        return try {
            val bigDecimal = BigDecimal(num)
            bigDecimal.stripTrailingZeros().toPlainString()
        } catch (e: Exception) {
            e.printStackTrace()
            "0"
        }
    }

    /**
     * 字符串截取小数点后number位
     * 末位不足，则补全 0
     *
     * @param number
     * @param count
     * @return
     */
    fun checkString(number: String, count: Int): String {
        var num = number
        if (num.contains(".")) {
            when {
                count == 0 -> {
                    num = num.substring(0, num.indexOf("."))
                }
                num.length - 1 - num.indexOf(".") >= count -> {
                    num = num.substring(0, num.indexOf(".") + count + 1)
                }
                num.length - 1 - num.indexOf(".") < count -> {
                    val add = count - (num.length - 1 - num.indexOf("."))
                    val builder = StringBuilder(num)
                    for (i in 0 until add) {
                        builder.append("0")
                    }
                    num = builder.toString()
                }
            }
        } else {
            if (count > 0) {
                val builder = StringBuilder(num)
                builder.append(".")
                for (i in 0 until count) {
                    builder.append("0")
                }
                num = builder.toString()
            }
        }
        return num
    }

    /**
     * 保证EditText中的字符串符合格式
     *
     * @param num
     * @return
     */
    fun formatEditText(num: String?): String {
        if (num == null) {
            return ""
        }
        if (num.isNotEmpty() && matchDecimal(num)) {
            return num
        }
        val n = num.replace(",", "")
        return formatEditText(n.toDouble())
    }

    fun formatEditText(num: Double): String {
        val nf = NumberFormat.getInstance()
        // 整数部分
        nf.maximumIntegerDigits = 100
        // 小数部分
        nf.maximumFractionDigits = 100
        nf.isGroupingUsed = true
        return nf.format(num)
    }

    fun formatString(charSequence: CharSequence, number: Int): CharSequence {
        if ('.' == charSequence[charSequence.length - 1] || "".contentEquals(charSequence)) {
            return charSequence
        }
        val index = charSequence.toString().indexOf(".")
        if (index != -1) {
            if (charSequence.length - index - 1 > number) {
                return charSequence.subSequence(0, index + number + 1)
            } else if (charSequence.isNotEmpty() && matchDecimal(charSequence.toString())) {
                return charSequence
            }
        }
        val sb = StringBuilder("#")
        if (number > 0) {
            sb.append(".")
            for (i in 1..number) {
                sb.append("#")
            }
        }
        val format = DecimalFormat(sb.toString())
        return format.format(charSequence.toString().toDouble())
    }

    /**
     * 符合如下条件的字符串小数不做格式化，eg: 0.000, 32.000
     *
     * @param num
     * @return
     */
    fun matchDecimal(num: String): Boolean {
        val pattern = Pattern.compile("^\\d+\\.0*")
        return pattern.matcher(num).find()
    }
}