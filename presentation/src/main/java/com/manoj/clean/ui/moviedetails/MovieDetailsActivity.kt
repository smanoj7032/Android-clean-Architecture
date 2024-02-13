package com.manoj.clean.ui.moviedetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.navigation.navArgs
import com.manoj.clean.R
import com.manoj.clean.databinding.ActivityMovieDetailsBinding
import com.manoj.clean.ui.common.base.BaseActivity
import com.manoj.clean.ui.common.customdialogs.CustomDialog
import com.manoj.clean.ui.common.customdialogs.DialogAnimation
import com.manoj.clean.ui.common.customdialogs.DialogStyle
import com.manoj.clean.ui.common.customdialogs.DialogType
import com.manoj.clean.ui.common.customdialogs.OnDialogClickListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MovieDetailsActivity : BaseActivity<ActivityMovieDetailsBinding>() ,View.OnClickListener{
    private val args: MovieDetailsActivityArgs by navArgs()
    var errorMessage: String = "A failure occurred during registration"
    var successMessage: String = "The message was sent successfully!"
    var warningMessage: String = "Please verify that you have completed all fields"
    var infoMessage: String = "Your request has been updated"
    var infoTitle: String = "Info"
    var successTitle: String = "Success"
    var errorTitle: String = "Error"
    var warningTitle: String = "Warning"


    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMovieDetailsBinding =
        ActivityMovieDetailsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        setViews()
    }

    private fun setViews() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.movie_details_container, MovieDetailsFragment.newInstance(args.movieId))
            .commitNow()
    }

    private fun setupActionBar() = supportActionBar?.apply {
        setDisplayShowTitleEnabled(false)
        setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        fun start(context: Context, movieId: Int) {
            val starter = Intent(context, MovieDetailsActivity::class.java)
            starter.putExtra("movieId", movieId)
            context.startActivity(starter)
        }
    }
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_flash_dialog_success -> {
                CustomDialog.Builder(this, DialogStyle.FLASH, DialogType.SUCCESS)
                    .setTitle(successTitle)
                    .setMessage(successMessage)
                    .setAnimation(DialogAnimation.DIAGONAL)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }

                        override fun onNegativeClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                    })
                    .show()
            }
            R.id.btn_flash_dialog_error -> {
                CustomDialog.Builder(this, DialogStyle.FLASH, DialogType.ERROR)
                    .setTitle(errorTitle)
                    .setMessage(errorMessage)
                    .setAnimation(DialogAnimation.SHRINK)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }

                        override fun onNegativeClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                    })
                    .show()
            }
            R.id.btn_connectify_dialog_success -> if (binding.rbConnectifyLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.CONNECTIFY, DialogType.SUCCESS)
                    .setTitle("Network found")
                    .setMessage("Internet connection established")
                    .setCancelable(false)
                    .setDuration(2000)
                    .show()
            } else {
                CustomDialog.Builder(this, DialogStyle.CONNECTIFY, DialogType.SUCCESS)
                    .setTitle("Network found")
                    .setMessage("Internet connection established")
                    .setCancelable(false)
                    .setDuration(2000)
                    .setDarkMode(true)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }

                        override fun onNegativeClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                    })
                    .show()
            }
            R.id.btn_connectify_dialog_error -> if (binding.rbConnectifyLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.CONNECTIFY, DialogType.ERROR)
                    .setTitle("Network unavailable")
                    .setMessage("No internet connection")
                    .setAnimation(DialogAnimation.SWIPE_LEFT)
                    .setDuration(2000)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }

                        override fun onNegativeClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                    })
                    .show()
            } else {
                CustomDialog.Builder(this, DialogStyle.CONNECTIFY, DialogType.ERROR)
                    .setTitle("Network unavailable")
                    .setMessage("No internet connection")
                    .setDarkMode(true)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }

                        override fun onNegativeClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                    })
                    .show()

            }
            R.id.btn_toaster_dialog_error -> if (binding.rbToasterLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.TOASTER, DialogType.ERROR)
                    .setTitle(errorTitle)
                    .setMessage(errorMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }

                        override fun onNegativeClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                    })
                    .show()

            } else {
                CustomDialog.Builder(this, DialogStyle.TOASTER, DialogType.ERROR)
                    .setTitle(errorTitle)
                    .setMessage(errorMessage)
                    .setDarkMode(true)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }

                        override fun onNegativeClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                    })
                    .show()
            }
            R.id.btn_toaster_dialog_success -> if (binding.rbToasterLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.TOASTER, DialogType.SUCCESS)
                    .setTitle(successTitle)
                    .setMessage(successMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                    })
                    .show()
            } else {
                CustomDialog.Builder(this, DialogStyle.TOASTER, DialogType.SUCCESS)
                    .setTitle(successTitle)
                    .setMessage(successMessage)
                    .setDarkMode(true)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                    })
                    .show()
            }
            R.id.btn_toaster_dialog_warning -> if (binding.rbToasterLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.TOASTER, DialogType.WARNING)
                    .setTitle(warningTitle)
                    .setMessage(warningMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            } else {
                CustomDialog.Builder(this, DialogStyle.TOASTER, DialogType.WARNING)
                    .setTitle(warningTitle)
                    .setMessage(warningMessage)
                    .setDarkMode(true)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            }
            R.id.btn_toaster_dialog_info -> if (binding.rbToasterLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.TOASTER, DialogType.INFO)
                    .setTitle(infoTitle)
                    .setMessage(infoMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            } else {
                CustomDialog.Builder(this, DialogStyle.TOASTER, DialogType.INFO)
                    .setTitle(infoTitle)
                    .setMessage(infoMessage)
                    .setDarkMode(true)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()

            }

            R.id.btn_drake_dialog_success -> {
                CustomDialog.Builder(this, DialogStyle.DRAKE, DialogType.SUCCESS)
                    .setTitle(successTitle)
                    .setMessage(successMessage)
                    .setAnimation(DialogAnimation.CARD)
                    .show()
            }

            R.id.btn_drake_dialog_error -> {
                CustomDialog.Builder(this, DialogStyle.DRAKE, DialogType.ERROR)
                    .setTitle(errorTitle)
                    .setAnimation(DialogAnimation.CARD)
                    .setMessage(errorMessage)
                    .show()
            }

            R.id.btn_emoji_dialog_success -> if (binding.rbEmojiLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.EMOJI, DialogType.SUCCESS)
                    .setTitle(successTitle)
                    .setMessage(errorMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            } else {
                CustomDialog.Builder(this, DialogStyle.EMOJI, DialogType.SUCCESS)
                    .setTitle(errorTitle)
                    .setMessage(errorMessage)
                    .setDarkMode(true)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            }

            R.id.btn_emoji_dialog_error -> if (binding.rbEmojiLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.EMOJI, DialogType.ERROR)
                    .setTitle(errorTitle)
                    .setMessage(errorMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            } else {
                CustomDialog.Builder(this, DialogStyle.EMOJI, DialogType.ERROR)
                    .setTitle(errorTitle)
                    .setMessage(errorMessage)
                    .setDarkMode(true)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            }

            R.id.btn_emotion_dialog_success -> {
                CustomDialog.Builder(this, DialogStyle.EMOTION, DialogType.SUCCESS)
                    .setTitle(successTitle)
                    .setMessage(successMessage)
                    .show()
            }

            R.id.btn_emotion_dialog_error -> {
                CustomDialog.Builder(this, DialogStyle.EMOTION, DialogType.ERROR)
                    .setTitle(errorTitle)
                    .setMessage(errorMessage)
                    .show()
            }

            R.id.btn_rainbow_dialog_error -> {
                CustomDialog.Builder(this, DialogStyle.RAINBOW, DialogType.ERROR)
                    .setTitle(errorTitle)
                    .setMessage(errorMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            }

            R.id.btn_rainbow_dialog_success -> {
                CustomDialog.Builder(this, DialogStyle.RAINBOW, DialogType.SUCCESS)
                    .setTitle(successTitle)
                    .setMessage(successMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            }

            R.id.btn_rainbow_dialog_warning -> {
                CustomDialog.Builder(this, DialogStyle.RAINBOW, DialogType.WARNING)
                    .setTitle(warningTitle)
                    .setMessage(warningMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            }

            R.id.btn_rainbow_dialog_info -> {
                CustomDialog.Builder(this, DialogStyle.RAINBOW, DialogType.INFO)
                    .setTitle(infoTitle)
                    .setMessage(infoMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            }

            R.id.btn_flat_dialog_success -> if (binding.rbFlatLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.FLAT, DialogType.SUCCESS)
                    .setTitle(successTitle)
                    .setMessage(successMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            } else {
                CustomDialog.Builder(this, DialogStyle.FLAT, DialogType.SUCCESS)
                    .setTitle(successTitle)
                    .setMessage(successMessage)
                    .setCancelable(false)
                    .setDarkMode(true)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            }
            R.id.btn_flat_dialog_error -> if (binding.rbFlatLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.FLAT, DialogType.ERROR)
                    .setTitle(errorTitle)
                    .setMessage(errorMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            } else {
                CustomDialog.Builder(this, DialogStyle.FLAT, DialogType.ERROR)
                    .setTitle(errorTitle)
                    .setMessage(errorMessage)
                    .setDarkMode(true)
                    .show()
            }
            R.id.btn_flat_dialog_warning -> if (binding.rbFlatLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.FLAT, DialogType.WARNING)
                    .setTitle(warningTitle)
                    .setMessage(warningMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            } else {
                CustomDialog.Builder(this, DialogStyle.FLAT, DialogType.WARNING)
                    .setTitle(warningTitle)
                    .setMessage(warningMessage)
                    .setDarkMode(true)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            Toast.makeText(applicationContext, "Good !", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            }
            R.id.btn_flat_dialog_info -> if (binding.rbFlatLight.isChecked) {
                CustomDialog.Builder(this, DialogStyle.FLAT, DialogType.INFO)
                    .setTitle(infoTitle)
                    .setMessage(infoMessage)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            } else {
                CustomDialog.Builder(this, DialogStyle.FLAT, DialogType.INFO)
                    .setTitle(infoTitle)
                    .setDuration(2000)
                    .setMessage(infoMessage)
                    .setDarkMode(true)
                    .setOnClickListener(object : OnDialogClickListener {
                        override fun onClick(dialog: CustomDialog.Builder) {
                            dialog.dismiss()
                        }
                        override fun onNegativeClick(dialog: CustomDialog.Builder) {dialog.dismiss()}
                    })
                    .show()
            }
        }
    }
}