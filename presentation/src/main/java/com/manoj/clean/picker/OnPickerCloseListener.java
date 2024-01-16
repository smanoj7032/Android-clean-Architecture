package com.manoj.clean.picker;

import android.net.Uri;

import com.manoj.clean.picker.ItemModel;

public interface OnPickerCloseListener {
    void onPickerClosed(@ItemModel.Companion.ItemType long type, Uri uri);
}
