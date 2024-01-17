package com.manoj.clean.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    private val pathPaint = Paint()

    init {
        pathPaint.color =
            ContextCompat.getColor(context, android.R.color.black) // Set your desired path color
        pathPaint.strokeWidth = 5f // Set your desired path stroke width
        pathPaint.style = Paint.Style.STROKE
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val childCount = parent.childCount
        val itemCount = parent.adapter?.itemCount ?: 0
        val columns = 5 // Number of columns in your grid view

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            if (position < itemCount - columns) {
                // Draw horizontal path
                val nextRowChild = parent.getChildAt(i + columns)
                if (nextRowChild != null) {
                    c.drawLine(
                        child.x + child.width / 2,
                        child.y + child.height / 2,
                        nextRowChild.x + nextRowChild.width / 2,
                        nextRowChild.y + nextRowChild.height / 2,
                        pathPaint
                    )
                }
            }

            if (position % columns != columns - 1) {
                // Draw vertical path
                val nextColumnChild = parent.getChildAt(i + 1)
                if (nextColumnChild != null) {
                    c.drawLine(
                        child.x + child.width / 2,
                        child.y + child.height / 2,
                        nextColumnChild.x + nextColumnChild.width / 2,
                        nextColumnChild.y + nextColumnChild.height / 2,
                        pathPaint
                    )
                }
            }
        }
    }

}
