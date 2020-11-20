package com.zjy.arch

import android.content.ContentUris
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_recycler.*
import kotlin.concurrent.thread

/**
 * @author zhengjy
 * @since 2020/08/27
 * Description:
 */
class RecyclerActivity : AppCompatActivity() {

    private val data = arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)

//        data.addAll(DATA)
//        refresh.setOnRefreshListener {
//            data.clear()
//            data.addAll(DATA)
//            rv_test.adapter?.notifyDataSetChanged()
//            refresh.isRefreshing = false
//        }
//        rv_test.layoutManager = LinearLayoutManager(this)
//        rv_test.adapter = object : RecyclerView.Adapter<TestViewHolder>() {
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
//                val root = layoutInflater.inflate(R.layout.item_recycler, parent, false)
//                return TestViewHolder(root)
//            }
//
//            override fun getItemCount(): Int {
//                return data.size
//            }
//
//            override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
//                Glide.with(this@RecyclerActivity)
//                    .load(data[position])
//                    .apply(RequestOptions().placeholder(R.drawable.bg_chart))
//                    .into(holder.image)
//            }
//
//        }

        findMediaStore()
    }

    private fun findMediaStore() {
        thread {
            val set = HashSet<String>()
            var filecount = 0
            var imagecount = 0
            var videocount = 0
            var audiocount = 0
            var thumbcount = 0
            val fileCursor = contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                arrayOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA
                ), null, null, null)
            if (fileCursor != null) {
                while (fileCursor.moveToNext()) {
                    filecount++
                    set.add(fileCursor.getString(1))
                    println("zhengjy file:id:${fileCursor.getString(0)},path:${fileCursor.getString(1)}")
                }
            }

            val imageCursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA
                ), null, null, null)
            if (imageCursor != null) {
                while (imageCursor.moveToNext()) {
                    imagecount++
                    set.remove(imageCursor.getString(1))
                    println("zhengjy img :id:${imageCursor.getString(0)},path:${imageCursor.getString(1)}")
                }
            }

            val videoCursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Video.VideoColumns._ID,
                    MediaStore.Video.VideoColumns.DATA
                ), null, null, null)
            if (videoCursor != null) {
                while (videoCursor.moveToNext()) {
                    videocount++
                    set.remove(videoCursor.getString(1))
                    println("zhengjy vid :id:${videoCursor.getString(0)},path:${videoCursor.getString(1)}")
                }
            }

            val audioCursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Audio.AudioColumns._ID,
                    MediaStore.Audio.AudioColumns.DATA
                ), null, null, null)
            if (audioCursor != null) {
                while (audioCursor.moveToNext()) {
                    audiocount++
                    set.remove(audioCursor.getString(1))
                    println("zhengjy aud :id:${audioCursor.getString(0)},path:${audioCursor.getString(1)}")
                }
            }

            val thumbCursor = contentResolver.query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.Thumbnails.DATA
                ), null, null, null)
            if (thumbCursor != null) {
                while (thumbCursor.moveToNext()) {
                    thumbcount++
                    set.remove(thumbCursor.getString(1))
                    println("zhengjy thu :id:${thumbCursor.getString(0)},path:${thumbCursor.getString(1)}")
                }
            }
            println("zhengjy:file:${filecount},image:${imagecount},video:${videocount},audio:${audiocount},thumb:${thumbcount}")
            println("zhengjy:${set}")


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val bitmap = contentResolver.loadThumbnail(
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        534219
                    ),
                    Size(200, 200),
                    null
                )
                runOnUiThread {
                    images.setImageBitmap(bitmap)
                }
            }
        }
    }
}

val DATA = arrayListOf(
    "a",
    "",
    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598446305183&di=a8b2fda791d58ba3789795c099c77903&imgtype=0&src=http%3A%2F%2Fimg3.redocn.com%2Ftupian%2F20150211%2Fchouxiangqicailuoxuanxuanzhuanxiantiaoguangdianzuhetusucaieps_3940262.jpg",
    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1598446322673&di=a7278e8c56d2dcf127baf738bddf357f&imgtype=0&src=http%3A%2F%2Fa-ssl.duitang.com%2Fuploads%2Fitem%2F201509%2F21%2F20150921115800_KdTcs.jpeg",
    "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3544005106,2960177055&fm=26&gp=0.jpg",
    "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2983633983,1878988042&fm=26&gp=0.jpg",
    "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2983633983,1878988042&fm=26&gp=0.jpg",
    "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2983633983,1878988042&fm=26&gp=0.jpg",
    "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2983633983,1878988042&fm=26&gp=0.jpg",
    "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2983633983,1878988042&fm=26&gp=0.jpg"
)

class TestViewHolder(root: View) : RecyclerView.ViewHolder(root) {

    val image = root.findViewById<ImageView>(R.id.iv_photo)
}