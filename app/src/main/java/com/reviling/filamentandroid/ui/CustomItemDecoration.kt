package com.reviling.filamentandroid.ui

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomItemDecoration(
    private val marginRight: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // Tambahkan margin kanan hanya untuk item terakhir
        if (position == itemCount - 1) {
            outRect.right = marginRight
        } else {
            outRect.right = 0 // Tidak ada margin untuk item lainnya
        }
    }
}

class CustomItemDecorationVertical(
    private val marginRight: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount
        Log.d("IsidariItemCount", itemCount.toString())
        // Tambahkan margin kanan hanya untuk item terakhir
        if (position == itemCount - 1) {
            Log.d("position", position.toString())
            outRect.bottom = marginRight
        } else {
            outRect.bottom = 0 // Tidak ada margin untuk item lainnya
        }
    }
}

class CustomItemDecorationVerticalUpperBottom(
    private val marginRight: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount
        Log.d("IsidariItemCount", itemCount.toString())
        // Tambahkan margin kanan hanya untuk item terakhir
        if (position == itemCount - 1) {
            Log.d("position", position.toString())
            outRect.bottom = marginRight
        } else {
            outRect.bottom = 0 // Tidak ada margin untuk item lainnya
        }

        if (position == 0) {
            Log.d("position", position.toString())
            outRect.top = marginRight
        } else {
            outRect.top = 0 // Tidak ada margin untuk item lainnya
        }
    }
}

class CustomItemDecorationVerticalDouble(
    private val marginRight: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount
        Log.d("IsidariItemCount", itemCount.toString())
        // Tambahkan margin kanan hanya untuk item terakhir
        if (position == itemCount - 1 || position == itemCount - 2) {
            outRect.bottom = marginRight  // atau nilai margin yang sesuai
        } else {
            outRect.bottom = 0 // Tidak ada margin untuk item lainnya
        }
    }
}

class CustomItemDecorationAbove(
    private val marginRight: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
//        val itemCount = state.itemCount

        // Tambahkan margin kanan hanya untuk item terakhir
        if (position == 0 || position == 1) {
            outRect.top = marginRight
        } else {
            outRect.bottom = 0 // Tidak ada margin untuk item lainnya
        }
    }
}

