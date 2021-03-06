package com.example.android.cinematik;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public class RecyclerViewItemDecorator extends RecyclerView.ItemDecoration {

    private int itemOffset;

    public RecyclerViewItemDecorator(int itemOffset) {
        this.itemOffset = itemOffset;
    }

    @SuppressWarnings("SameParameterValue")
    public RecyclerViewItemDecorator(Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelOffset(itemOffsetId));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(itemOffset, itemOffset, itemOffset, itemOffset);
    }
}
