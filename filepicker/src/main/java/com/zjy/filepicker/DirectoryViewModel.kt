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

import android.app.Application
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zjy.filepicker.comparator.NameComparator
import kotlinx.coroutines.*

/**
 * ViewModel for the [DirectoryFragment].
 */
class DirectoryViewModel(application: Application) : AndroidViewModel(application) {
    private val _documents = MutableLiveData<List<CachingDocumentFile>>()
    val documents: LiveData<List<CachingDocumentFile>> = _documents

    private val _openDirectory = MutableLiveData<Event<CachingDocumentFile>>()
    val openDirectory: LiveData<Event<CachingDocumentFile>> = _openDirectory

    private val _openDocument = MutableLiveData<Event<CachingDocumentFile>>()
    val openDocument: LiveData<Event<CachingDocumentFile>> = _openDocument

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    private var cancelLoadingView = false

    fun loadDirectory(directoryUri: Uri, showLoading: Boolean) {
        val documentsTree = DocumentFile.fromTreeUri(getApplication(), directoryUri) ?: return
        if (showLoading) {
            cancelLoadingView = false
            viewModelScope.launch {
                // 延迟50ms后显示loading
                delay(50)
                if (!cancelLoadingView) {
                    _loading.postValue(true)
                }
            }
        }

        // It's much nicer when the documents are sorted by something, so we'll sort the documents
        // we got by name. Unfortunate there may be quite a few documents, and sorting can take
        // some time, so we'll take advantage of coroutines to take this work off the main thread.
        viewModelScope.launch(Dispatchers.IO) {
            val childDocuments = documentsTree.listFiles().toCachingList(getApplication())
            val sortedDocuments = childDocuments.asSequence().filter {
                it.name?.startsWith(".") == false
            }.sortedWith(NameComparator()).toMutableList()
            cancelLoadingView = true
            _loading.postValue(false)
            _documents.postValue(sortedDocuments)
        }
    }

    /**
     * Method to dispatch between clicking on a document (which should be opened), and
     * a directory (which the user wants to navigate into).
     */
    fun documentClicked(clickedDocument: CachingDocumentFile) {
        if (clickedDocument.isDirectory) {
            _openDirectory.postValue(Event(clickedDocument))
        } else {
            _openDocument.postValue(Event(clickedDocument))
        }
    }
}