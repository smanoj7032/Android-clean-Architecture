package com.manoj.clean.picker

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import com.manoj.clean.R
import com.manoj.clean.util.show

class CustomPickerDialog(context: Context) :
    Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {

    init {
        setContentView(R.layout.custom_dialog_layout)
    }

    fun showPreview(type: Long, uri: Uri, ok: (Long, Uri) -> Unit) {
        when (type) {
            ItemModel.ITEM_CAMERA, ItemModel.ITEM_GALLERY -> {
                val imageViewPreview = findViewById<ImageView>(R.id.imageViewPreview)
                imageViewPreview.show()
                imageViewPreview.setImageURI(uri)
            }

            ItemModel.ITEM_VIDEO, ItemModel.ITEM_VIDEO_GALLERY -> {
                val videoViewPreview = findViewById<VideoView>(R.id.videoViewPreview)
                videoViewPreview.show()
                videoViewPreview.setVideoURI(uri)
                videoViewPreview.setOnPreparedListener {
                    it.start()
                }
            }

            ItemModel.ITEM_FILES -> {
                Toast.makeText(context, "Type : $type----->>>   Uri : $uri", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        findViewById<Button>(R.id.btnOk).setOnClickListener {
            ok.invoke(type, uri)
            dismiss()
        }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { dismiss() }
        show()
    }
}
