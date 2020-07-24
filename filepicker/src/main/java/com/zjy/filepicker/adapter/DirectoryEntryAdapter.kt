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

package com.zjy.filepicker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.zjy.filepicker.CachingDocumentFile
import com.zjy.filepicker.R

class DirectoryEntryAdapter(
    private val context: Context,
    private val clickListeners: ClickListeners
) : RecyclerView.Adapter<DirectoryEntryAdapter.ViewHolder>() {

    private val directoryEntries = mutableListOf<CachingDocumentFile>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.directory_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        with(viewHolder) {
            val item = directoryEntries[position]
            if (item.isDirectory) {
                imageView.setImageResource(R.mipmap.folder)
            } else {
                when (item.ext) {
                    "apk" -> imageView.setImageResource(R.mipmap.apk)
                    "avi" -> imageView.setImageResource(R.mipmap.avi)
                    "doc", "docx" -> imageView.setImageResource(R.mipmap.doc)
                    "exe" -> imageView.setImageResource(R.mipmap.exe)
                    "flv" -> imageView.setImageResource(R.mipmap.flv)
                    "gif" -> {
                        val options: RequestOptions = RequestOptions()
                            .centerCrop()
                            .placeholder(R.mipmap.gif)
                        Glide
                            .with(context)
                            .load(item.uri)
                            .apply(options)
                            .into(imageView)
                    }
                    "jpg", "jpeg", "png" -> {
                        val options2: RequestOptions = RequestOptions()
                            .centerCrop()
                            .placeholder(R.mipmap.png)
                        Glide
                            .with(context)
                            .load(item.uri)
                            .apply(options2)
                            .into(imageView)
                    }
                    "mp3" -> imageView.setImageResource(R.mipmap.mp3)
                    "mp4", "f4v" -> imageView.setImageResource(R.mipmap.movie)
                    "pdf" -> imageView.setImageResource(R.mipmap.pdf)
                    "ppt", "pptx" -> imageView.setImageResource(R.mipmap.ppt)
                    "wav" -> imageView.setImageResource(R.mipmap.wav)
                    "xls", "xlsx" -> imageView.setImageResource(R.mipmap.xls)
                    "zip" -> imageView.setImageResource(R.mipmap.zip)
                    "ext" -> if (item.isDirectory) {
                        imageView.setImageResource(R.mipmap.folder)
                    } else {
                        imageView.setImageResource(R.mipmap.documents)
                    }
                    else -> if (item.isDirectory) {
                        imageView.setImageResource(R.mipmap.folder)
                    } else {
                        imageView.setImageResource(R.mipmap.documents)
                    }
                }
            }

            fileName.text = item.name
            mimeType.text = item.type ?: ""

            root.setOnClickListener {
                clickListeners.onDocumentClicked(item)
            }
            root.setOnLongClickListener {
                clickListeners.onDocumentLongClicked(item)
                true
            }
        }
    }

    override fun getItemCount() = directoryEntries.size

    fun setEntries(newList: List<CachingDocumentFile>) {
        synchronized(directoryEntries) {
            directoryEntries.clear()
            directoryEntries.addAll(newList)
            notifyDataSetChanged()
        }
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val fileName: TextView = view.findViewById(R.id.file_name)
        val mimeType: TextView = view.findViewById(R.id.mime_type)
        val imageView: ImageView = view.findViewById(R.id.entry_image)
    }
}

interface ClickListeners {
    fun onDocumentClicked(clickedDocument: CachingDocumentFile)
    fun onDocumentLongClicked(clickedDocument: CachingDocumentFile)
}
