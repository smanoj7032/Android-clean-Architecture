package com.manoj.clean.ui.common.imageslider.adapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.manoj.clean.ui.common.imageslider.ImageCarousel
import com.manoj.clean.ui.common.imageslider.model.CarouselGravity
import com.manoj.clean.ui.common.imageslider.model.CarouselItem
import com.manoj.clean.ui.common.imageslider.model.CarouselType

class InfiniteCarouselAdapter(
    recyclerView: RecyclerView,
    carouselType: CarouselType,
    carouselGravity: CarouselGravity,
    autoWidthFixing: Boolean,
    imageScaleType: ImageView.ScaleType,
    imagePlaceholder: Drawable?
) : FiniteCarouselAdapter(
    recyclerView,
    carouselType,
    carouselGravity,
    autoWidthFixing,
    imageScaleType,
    imagePlaceholder
) {
    override fun getItemCount(): Int {
        return if (dataList.isEmpty()) 0 else Integer.MAX_VALUE
    }

    override fun getItem(position: Int): CarouselItem? {
        return if (position < itemCount) {
            dataList[position % dataList.size]
        } else {
            null
        }
    }

    override fun getRealDataPosition(position: Int): Int {
        if (dataList.size == 0) return ImageCarousel.NO_POSITION
        return position % dataList.size
    }
}
