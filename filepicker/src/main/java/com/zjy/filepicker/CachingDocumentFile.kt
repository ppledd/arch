/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zjy.filepicker

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile

/**
 * Caching version of a [DocumentFile].
 *
 * A [DocumentFile] will perform a lookup (via the system [ContentResolver]), whenever a
 * property is referenced. This means that a request for [DocumentFile.getName] is a *lot*
 * slower than one would expect.
 *
 * To improve performance in the app, where we want to be able to sort a list of [DocumentFile]s
 * by name, we wrap it like this so the value is only looked up once.
 */
data class CachingDocumentFile(val name: String?, val type: String?, val size: Long, val uri: Uri) {
    val isDirectory: Boolean get() = type == null
    val ext: String by lazy { getExtension(name) }
}

private fun getExtension(pathOrUrl: String?): String {
    if (pathOrUrl == null) {
        return "ext"
    }
    val dotPos = pathOrUrl.lastIndexOf('.')
    return if (0 <= dotPos) {
        pathOrUrl.substring(dotPos + 1)
    } else {
        "ext"
    }
}

fun Array<DocumentFile>.toCachingList(context: Context): List<CachingDocumentFile> {
    val list = mutableListOf<CachingDocumentFile>()
    for (document in this) {
        document.apply {
            val resolver = context.contentResolver
            try {
                resolver.query(
                    uri,
                    arrayOf(
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_MIME_TYPE,
                        DocumentsContract.Document.COLUMN_SIZE,
                    ), null, null, null
                )?.use {
                    if (it.moveToFirst()) {
                        val name = it.getString(0)
                        val rawType = it.getString(1)
                        val length = it.getLong(2)
                        list += CachingDocumentFile(
                            name,
                             if (DocumentsContract.Document.MIME_TYPE_DIR == rawType) null else rawType,
                            length,
                            uri
                        )
                    }
                }
            } catch (e: Exception) {
                Log.w("CachingDocumentFile", "Failed query: $e")
            }
        }
    }
    return list
}