package com.manoj.clean.picker

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.IntDef
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.manoj.clean.util.parcelableArrayList
import kotlinx.android.synthetic.main.dialog_picker.*
import kotlinx.android.synthetic.main.item_picker_grid.view.*
import java.io.IOException

class PickerDialog(private val activity: AppCompatActivity?) : BottomSheetDialogFragment() {
    var fragment: Fragment? = null
    private var uri: Uri? = null
    private var fileName = ""
    var onPickerCloseListener: OnPickerCloseListener? = null

    private var dialogTitle = ""
    private var dialogTitleId = 0
    private var dialogTitleSize = 0F
    private var dialogTitleColor = 0

    @ListType
    private var dialogListType = TYPE_LIST
    private var dialogGridSpan = 3
    private var dialogItems = ArrayList<ItemModel>()


    /** ACTIVITY RESULT LAUNCHER */
    private val takePhoto: ActivityResultLauncher<Uri>? =
        activity?.registerForActivityResult(ActivityResultContracts.TakePicture())
        { isSaved ->
            if (isSaved) {
                onPickerCloseListener?.onPickerClosed(ItemModel.ITEM_CAMERA, uri)
                dismiss()
            }
        }
    private lateinit var openGallery: ActivityResultLauncher<Intent>
    private lateinit var recordVideo: ActivityResultLauncher<Intent>
    private lateinit var chooseVideo: ActivityResultLauncher<Intent>
    private lateinit var selectFile: ActivityResultLauncher<Intent>


    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_TITLE_ID = "titleId"
        private const val ARG_TITLE_SIZE = "titleSize"
        private const val ARG_TITLE_COLOR = "titleColor"
        private const val ARG_LIST_TYPE = "list"
        private const val ARG_GRID_SPAN = "gridSpan"
        private const val ARG_ITEMS = "items"

        const val REQUEST_PERMISSION_CAMERA = 1001
        const val REQUEST_PERMISSION_GALLERY = 1002
        const val REQUEST_PERMISSION_VIDEO = 1003
        const val REQUEST_PERMISSION_VGALLERY = 1004
        const val REQUEST_PERMISSION_FILE = 1005

        const val REQUEST_PICK_PHOTO = 1102
        const val REQUEST_VIDEO = 1103
        const val REQUEST_PICK_FILE = 1104

        private fun newInstance(
            activity: AppCompatActivity?,
            fragment: Fragment?,
            dialogTitle: String,
            dialogTitleId: Int,
            dialogTitleSize: Float,
            dialogTitleColor: Int,
            dialogListType: Long,
            dialogGridSpan: Int,
            dialogItems: ArrayList<ItemModel>
        ): PickerDialog {

            val args = Bundle()

            args.putString(ARG_TITLE, dialogTitle)
            args.putInt(ARG_TITLE_ID, dialogTitleId)
            args.putFloat(ARG_TITLE_SIZE, dialogTitleSize)
            args.putInt(ARG_TITLE_COLOR, dialogTitleColor)
            args.putLong(ARG_LIST_TYPE, dialogListType)
            args.putInt(ARG_GRID_SPAN, dialogGridSpan)
            args.putParcelableArrayList(ARG_ITEMS, dialogItems)

            val dialog = PickerDialog(activity)
            dialog.arguments = args
            dialog.fragment = fragment

            return dialog
        }

        @IntDef(TYPE_LIST, TYPE_GRID)
        @Retention(AnnotationRetention.SOURCE)
        annotation class ListType

        const val TYPE_LIST = 0L
        const val TYPE_GRID = 1L
    }

    class Builder {
        private var activity: AppCompatActivity? = null
        private var fragment: Fragment? = null

        private var dialogTitle = ""
        private var dialogTitleId = 0
        private var dialogTitleSize = 0F
        private var dialogTitleColor = 0

        @ListType
        private var dialogListType = TYPE_LIST
        private var dialogGridSpan = 3
        private var dialogItems = ArrayList<ItemModel>()

        constructor(activity: AppCompatActivity) {
            this.activity = activity
        }

        constructor(fragment: Fragment) {
            this.fragment = fragment
        }

        fun setTitle(title: String): Builder {
            dialogTitle = title
            return this
        }

        fun setTitle(title: Int): Builder {
            dialogTitleId = title
            return this
        }

        fun setTitleTextSize(textSize: Float): Builder {
            dialogTitleSize = textSize
            return this
        }

        fun setTitleTextColor(textColor: Int): Builder {
            dialogTitleColor = textColor
            return this
        }

        fun setListType(@ListType type: Long, gridSpan: Int = 3): Builder {
            dialogListType = type
            dialogGridSpan = gridSpan
            return this
        }

        fun setItems(items: ArrayList<ItemModel>): Builder {
            items.forEachIndexed { i, itemModel ->
                items.forEachIndexed { j, itemModel2 ->
                    if (i != j && itemModel2.type == itemModel.type) {
                        throw IllegalStateException("You cannot have two similar item models in this list")
                    }
                }
            }
            dialogItems = items
            return this
        }

        fun create(): PickerDialog {
            return newInstance(
                activity,
                fragment,
                dialogTitle,
                dialogTitleId,
                dialogTitleSize,
                dialogTitleColor,
                dialogListType,
                dialogGridSpan,
                dialogItems
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(Lyt.dialog_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        createTitle()
        createList()
        setLauncher()
    }

    private fun getData() {
        val args = arguments ?: return

        dialogTitle = args.getString(ARG_TITLE).toString()
        dialogTitleId = args.getInt(ARG_TITLE_ID)
        dialogTitleSize = args.getFloat(ARG_TITLE_SIZE)
        dialogTitleColor = args.getInt(ARG_TITLE_COLOR)
        dialogListType = args.getLong(ARG_LIST_TYPE)
        dialogGridSpan = args.getInt(ARG_GRID_SPAN)
        dialogItems = args.parcelableArrayList(ARG_ITEMS)!!
    }

    private fun createTitle() {
        if (dialogTitle == "" && dialogTitleId == 0) {
            pickerTitle.visibility = View.GONE
            return
        }

        if (dialogTitle == "") {
            pickerTitle set dialogTitleId
        } else {
            pickerTitle set dialogTitle
        }

        if (dialogTitleSize != 0F) {
            pickerTitle.textSize = dialogTitleSize
        }

        pickerTitle.setTextColor(
            if (dialogTitleColor == 0) ContextCompat.getColor(requireContext(), Clr.colorDark)
            else dialogTitleColor
        )
    }

    private fun createList() {
        val viewItem =
            if (dialogListType == TYPE_LIST) Lyt.item_picker_list else Lyt.item_picker_grid
        val manager = if (dialogListType == TYPE_LIST)
            LinearLayoutManager(context) else GridLayoutManager(context, dialogGridSpan)

        pickerItems.init(
            dialogItems,
            viewItem,
            manager,
            { item: ItemModel, _: Int ->
                initIconBackground(item, this)
                initIcon(item, icon)
                initLabel(item, label)
            },
            { item: ItemModel, _: Int ->
                when (item.type) {
                    ItemModel.ITEM_CAMERA -> openCamera()

                    ItemModel.ITEM_GALLERY -> openGallery()

                    ItemModel.ITEM_VIDEO -> openVideoCamera()

                    ItemModel.ITEM_VIDEO_GALLERY -> openVideoGallery()

                    ItemModel.ITEM_FILES -> openFilePicker()
                }
            }
        )
    }

    private fun initIconBackground(item: ItemModel, view: View) {
        if (item.hasBackground) {
            val color = if (item.itemBackgroundColor == 0)
                ContextCompat.getColor(view.context, Clr.colorAccent)
            else item.itemBackgroundColor

            val bg: Drawable?

            when (item.backgroundType) {
                ItemModel.TYPE_SQUARE -> {
                    bg = ContextCompat.getDrawable(view.context, Drw.bg_square)
                    bg?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                }

                ItemModel.TYPE_ROUNDED_SQUARE -> {
                    bg = ContextCompat.getDrawable(view.context, Drw.bg_rounded_square)
                    bg?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                }

                else -> {
                    bg = ContextCompat.getDrawable(view.context, Drw.bg_circle)
                    bg?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                }
            }

            view.icon.background = bg
        }
    }

    private fun initIcon(item: ItemModel, icon: AppCompatImageView) {
        if (item.itemIcon == 0) {
            icon set when (item.type) {
                ItemModel.ITEM_GALLERY -> Drw.ic_image
                ItemModel.ITEM_VIDEO -> Drw.ic_videocam
                ItemModel.ITEM_VIDEO_GALLERY -> Drw.ic_video_library
                ItemModel.ITEM_FILES -> Drw.ic_file
                else -> Drw.ic_camera
            }
        } else {
            icon set item.itemIcon
        }
    }

    private fun initLabel(item: ItemModel, label: AppCompatTextView) {
        if (item.itemLabel == "") {
            label set when (item.type) {
                ItemModel.ITEM_GALLERY -> Str.gallery
                ItemModel.ITEM_VIDEO -> Str.video
                ItemModel.ITEM_VIDEO_GALLERY -> Str.vgallery
                ItemModel.ITEM_FILES -> Str.file
                else -> Str.photo
            }
        } else {
            label set item.itemLabel
        }
    }

    fun onPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_CAMERA -> if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }

            REQUEST_PERMISSION_GALLERY -> if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            }

            REQUEST_PERMISSION_VIDEO -> if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                openVideoCamera()
            }

            REQUEST_PERMISSION_VGALLERY -> if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                openVideoGallery()
            }

            REQUEST_PERMISSION_FILE -> if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                openFilePicker()
            }
        }
    }

    private fun openCamera() {
        uri = MediaUtils.getUriFromFile(
            requireContext(), MediaUtils.getMakeFile(requireContext(), ".png")
        )
        takePhoto?.launch(uri)
    }

    private fun openGallery() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        openGallery.launch(pickPhoto)
    }

    private fun openVideoCamera() {
        fileName = (System.currentTimeMillis() / 1000).toString() + ".mp4"

        val takeVideo = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        takeVideo.putExtra(
            MediaStore.EXTRA_OUTPUT,
            Environment.getExternalStorageDirectory().absolutePath + "/" + fileName
        )
        recordVideo.launch(takeVideo)
    }

    private fun openVideoGallery() {
        val pickVideo =
            Intent(Intent.ACTION_PICK)
        pickVideo.type="video/*"
        chooseVideo.launch(pickVideo)
    }

    private fun openFilePicker() {
        val pickFile = Intent(Intent.ACTION_GET_CONTENT)
        pickFile.type = "*/*"
        selectFile.launch(pickFile)
    }

    private fun onActivityResult(requestCode: Int, resultCode: ActivityResult) {
        if (resultCode.resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_PICK_PHOTO -> pickPhoto(resultCode.data)
                REQUEST_VIDEO -> pickVideo(resultCode.data)
                REQUEST_PICK_FILE -> pickFile(resultCode.data)
            }
        }
    }

    private fun pickPhoto(data: Intent?) {
        if (data == null) {
            return
        }

        val uri = data.data ?: return

        if (onPickerCloseListener != null) {
            onPickerCloseListener?.onPickerClosed(ItemModel.ITEM_GALLERY, uri)
        }
        dismiss()
    }

    private fun pickVideo(data: Intent?) {
        if (data == null) {
            return
        }

        val uri = data.data ?: return

        if (onPickerCloseListener != null) {
            onPickerCloseListener?.onPickerClosed(ItemModel.ITEM_VIDEO_GALLERY, uri)
        }
        dismiss()
    }

    private fun pickFile(data: Intent?) {
        if (data == null) {
            return
        }

        val uri = data.data ?: return

        if (onPickerCloseListener != null) {
            onPickerCloseListener?.onPickerClosed(ItemModel.ITEM_FILES, uri)
        }
        dismiss()
    }

    fun setPickerCloseListener(onClose: (Long, Uri) -> Unit) {
        onPickerCloseListener = OnPickerCloseListener(onClose)
    }

    fun show() {
        activity?.supportFragmentManager?.let { show(it, "") }
    }

    private fun setLauncher() {
        openGallery =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(REQUEST_PICK_PHOTO, result)
            }
        recordVideo =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(REQUEST_VIDEO, result)
            }
        chooseVideo =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(REQUEST_VIDEO, result)
            }
        selectFile =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(REQUEST_PICK_FILE, result)
            }
    }
}