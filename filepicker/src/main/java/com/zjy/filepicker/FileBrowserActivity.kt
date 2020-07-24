package com.zjy.filepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.activity_file_browser.*

/**
 * @author zhengjy
 * @since 2020/07/23
 * Description:
 */
class FileBrowserActivity : AppCompatActivity() {

    companion object {
        private const val OPEN_DIRECTORY_REQUEST_CODE = 0xf11e
        private const val PREF_NAME = "com.zjy.filepicker.sharePreference"
        private const val DIRECTORY_ROOT = "DIRECTORY_ROOT"
    }

    private val preferences by lazy {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_browser)
        tv_title.text = "文件选择"
        iv_back.setOnClickListener { onBackPressed() }

        val root = preferences.getString(DIRECTORY_ROOT, null)?.toUri()
        if (root != null) {
            showDirectoryContents(root)
        } else {
            openDirectory()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val directoryUri = data?.data ?: return
            contentResolver.takePersistableUriPermission(
                directoryUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            preferences.edit().putString(DIRECTORY_ROOT, directoryUri.toString()).apply()
            repeat(supportFragmentManager.backStackEntryCount) {
                supportFragmentManager.popBackStack()
            }
            showDirectoryContents(directoryUri)
        }
    }

    fun showDirectoryContents(directoryUri: Uri) {
        supportFragmentManager.commit {
            val directoryTag = directoryUri.toString()
            val directoryFragment = DirectoryFragment.newInstance(directoryUri)
            setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out,
                R.anim.slide_right_in, R.anim.slide_right_out)
            add(R.id.fcv_container, directoryFragment, directoryTag)
            addToBackStack(directoryTag)
        }
    }

    // @RequiresApi(Build.VERSION_CODES.Q)
    private fun openDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        }
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
