package com.zjy.filepicker.comparator

import com.zjy.filepicker.CachingDocumentFile

/**
 * @author zhengjy
 * @since 2020/07/23
 * Description:
 */
class NameComparator : Comparator<CachingDocumentFile> {

    override fun compare(o1: CachingDocumentFile?, o2: CachingDocumentFile?): Int {
        return if (o1 == null || o2 == null) {
            if (o1 == null) {
                -1
            } else {
                1
            }
        } else {
            if (o1.isDirectory && !o2.isDirectory) {
                -1
            } else if (!o1.isDirectory && o2.isDirectory) {
                1
            } else {
                val s1: String = o1.name ?: ""
                val s2: String = o2.name ?: ""
                s1.compareTo(s2, ignoreCase = true)
            }
        }
    }
}