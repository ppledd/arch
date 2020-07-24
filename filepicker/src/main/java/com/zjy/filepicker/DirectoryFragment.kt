/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zjy.filepicker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zjy.filepicker.adapter.ClickListeners
import com.zjy.filepicker.adapter.DirectoryEntryAdapter
import kotlinx.android.synthetic.main.fragment_directory.*

/**
 * Fragment that shows a list of documents in a directory.
 */
class DirectoryFragment : Fragment() {
    private lateinit var directoryUri: Uri

    private lateinit var adapter: DirectoryEntryAdapter

    private lateinit var viewModel: DirectoryViewModel

    private var initLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_directory, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DirectoryEntryAdapter(requireContext(), object : ClickListeners {
            override fun onDocumentClicked(clickedDocument: CachingDocumentFile) {
                viewModel.documentClicked(clickedDocument)
            }

            override fun onDocumentLongClicked(clickedDocument: CachingDocumentFile) {

            }
        })
        recyclerView.adapter = adapter
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        directoryUri = arguments?.getString(ARG_DIRECTORY_URI)?.toUri()
            ?: throw IllegalArgumentException("Must pass URI of directory to open")

        viewModel = ViewModelProvider(this).get(DirectoryViewModel::class.java)

        viewModel.documents.observe(viewLifecycleOwner, Observer { documents ->
            if (documents.isNullOrEmpty()) {
                empty_tips.visibility = View.VISIBLE
                list.visibility = View.GONE
            } else {
                list.visibility = View.VISIBLE
                empty_tips.visibility = View.GONE
                adapter.setEntries(documents)
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                loading.visibility = View.VISIBLE
            } else {
                loading.visibility = View.GONE
            }
        })

        viewModel.openDirectory.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { directory ->
                (activity as FileBrowserActivity?)?.showDirectoryContents(directory.uri)
            }
        })

        viewModel.openDocument.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { document ->
                openDocument(document)
            }
        })

        viewModel.loadDirectory(directoryUri, initLoad)
        if (initLoad) {
            initLoad = false
        }
    }

    private fun openDocument(document: CachingDocumentFile) {
        try {
            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                data = document.uri
            }
            startActivity(openIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.error_no_activity, document.name),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {

        /**
         * Convenience method for constructing a [DirectoryFragment] with the directory uri
         * to display.
         */
        @JvmStatic
        fun newInstance(directoryUri: Uri) =
            DirectoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DIRECTORY_URI, directoryUri.toString())
                }
            }

        private const val ARG_DIRECTORY_URI =
            "com.example.android.directoryselection.ARG_DIRECTORY_URI"
    }
}
