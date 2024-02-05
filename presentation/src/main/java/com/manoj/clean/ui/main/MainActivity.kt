package com.manoj.clean.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.manoj.clean.R
import com.manoj.clean.databinding.ActivityMainBinding
import com.manoj.clean.picker.CustomPickerDialog
import com.manoj.clean.picker.ItemModel
import com.manoj.clean.picker.PickerDialog
import com.manoj.clean.ui.common.base.BaseActivity
import com.manoj.clean.ui.common.base.common.permissionutils.QuickPermissionsOptions
import com.manoj.clean.ui.common.base.common.permissionutils.runWithPermissions
import com.manoj.clean.ui.search.SearchActivity
import com.manoj.clean.util.PERMISSION_READ_STORAGE
import com.manoj.clean.util.getLocationPermissions
import dagger.hilt.android.AndroidEntryPoint


@RequiresApi(Build.VERSION_CODES.P)
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), OnMapReadyCallback {
    private lateinit var pickerDialog: PickerDialog
    private val navController by lazy { binding.container.getFragment<NavHostFragment>().navController }
    val locationLatitude = 31.4653170
    val locationLongitude = 76.7249976
    val formatString = "geo:%f,%f?q=%s"


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
        setMap()
    }

    private fun setMap() =
        runWithPermissions(
            *getLocationPermissions(),
            options = QuickPermissionsOptions(handleRationale = true)
        ) {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.fragment_detail) as SupportMapFragment
            mapFragment.getMapAsync(this)
            Log.e("T-----AG", "setMap: ")
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

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        val latLng = LatLng(locationLatitude, locationLongitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        googleMap.clear()
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.addMarker(markerOptions)
    }

}