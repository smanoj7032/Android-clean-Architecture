package com.manoj.clean.ui.common.customdialogs


interface OnDialogClickListener {
    fun onClick(dialog: CustomDialog.Builder)
    fun onNegativeClick(dialog: CustomDialog.Builder)
}