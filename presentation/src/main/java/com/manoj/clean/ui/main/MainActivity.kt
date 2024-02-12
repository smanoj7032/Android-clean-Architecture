package com.manoj.clean.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.manoj.clean.R
import com.manoj.clean.databinding.ActivityMainBinding
import com.manoj.clean.picker.CustomPickerDialog
import com.manoj.clean.picker.ItemModel
import com.manoj.clean.picker.PickerDialog
import com.manoj.clean.ui.common.base.BaseActivity
import com.manoj.clean.ui.common.base.common.permissionutils.runWithPermissions
import com.manoj.clean.ui.search.SearchActivity
import com.manoj.clean.util.PERMISSION_READ_STORAGE
import dagger.hilt.android.AndroidEntryPoint


@RequiresApi(Build.VERSION_CODES.P)
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var pickerDialog: PickerDialog
    private val navController by lazy { binding.container.getFragment<NavHostFragment>().navController }


    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(inflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.navigationView.background = null
        setupActionBar()
        initViews()
        setupNavigationView()
    }





    private fun initViews() {
        setPickerDialog()
        binding.fabAdd.setOnClickListener {
            runWithPermissions(
                Manifest.permission.CAMERA,
                *PERMISSION_READ_STORAGE
            ) { pickerDialog.show() }
        }
    }

    private fun setPickerDialog() {
        val items: ArrayList<ItemModel> = arrayListOf(
            ItemModel(ItemModel.ITEM_CAMERA, itemIcon = R.drawable.ic_camera_svg),
            ItemModel(ItemModel.ITEM_GALLERY, itemIcon = R.drawable.ic_gallery_svg),
            ItemModel(ItemModel.ITEM_VIDEO, itemIcon = R.drawable.ic_camera_svg),
            ItemModel(ItemModel.ITEM_VIDEO_GALLERY, itemIcon = R.drawable.ic_gallery_svg),
            ItemModel(ItemModel.ITEM_FILES, itemIcon = R.drawable.ic_camera_svg)
        )
        pickerDialog = PickerDialog.Builder(this).setTitle("Select Media")/*.setTitleTextSize(25f)
            .setTitleTextColor(R.color.colorDialogBg)*/.setListType(PickerDialog.TYPE_GRID, 3)
            .setItems(items)
            .create()

        pickerDialog.setPickerCloseListener { type, uri ->
            CustomPickerDialog(this).showPreview(type, uri) { _, _ -> }
            when (type) {
                ItemModel.ITEM_CAMERA -> {
                    Toast.makeText(
                        this,
                        "Type : $type----->>>   Uri : $uri",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                ItemModel.ITEM_GALLERY -> {
                    Toast.makeText(
                        this,
                        "Type : $type----->>>   Uri : $uri",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                ItemModel.ITEM_VIDEO -> {
                    Toast.makeText(
                        this,
                        "Type : $type----->>>   Uri : $uri",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                ItemModel.ITEM_VIDEO_GALLERY -> {
                    Toast.makeText(
                        this,
                        "Type : $type----->>>   Uri : $uri",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                ItemModel.ITEM_FILES -> {
                    Toast.makeText(
                        this,
                        "Type : $type----->>>   Uri : $uri",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupActionBar() = NavigationUI.setupActionBarWithNavController(
        this,
        navController,
        AppBarConfiguration(
            setOf(
                R.id.feedFragment,
                R.id.favoritesFragment,
                R.id.popularMoviesFragment,
                R.id.profileFragment
            )
        )
    )

    private fun setupNavigationView() = with(binding.navigationView) {
        setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        /*  @DrawableRes val darkModeIcon: Int =
              if (isDarkModeEnabled()) R.drawable.ic_dark_mode_fill else R.drawable.ic_dark_mode
          menu?.findItem(R.id.action_dark_mode)?.icon = ContextCompat.getDrawable(this, darkModeIcon)*/
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> SearchActivity.start(this)
            /* R.id.action_dark_mode -> {
                 enableDarkMode(!isDarkModeEnabled())
                 recreate()
             }*/
        }
        return true
    }

}