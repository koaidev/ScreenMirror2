package com.bangbangcoding.screenmirror.web.ui.studio.itemdecorations;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bangbangcoding.screenmirror.R;


public class GridDividerDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{16843284};
    private Drawable mDivider;
    private int mInsets;

    public GridDividerDecoration(Context context) {
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        this.mDivider = a.getDrawable(0);
        a.recycle();
        this.mInsets = context.getResources().getDimensionPixelSize(R.dimen.grid_insets);
    }

    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
        drawHorizontal(c, parent);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        if (parent.getChildCount() != 0) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            View child = parent.getChildAt(0);
            if (child.getHeight() != 0) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = (child.getBottom() + params.bottomMargin) + this.mInsets;
                int bottom = top + this.mDivider.getIntrinsicHeight();
                int parentBottom = parent.getHeight() - parent.getPaddingBottom();
                while (bottom < parentBottom) {
                    this.mDivider.setBounds(left, top, right, bottom);
                    this.mDivider.draw(c);
                    top += (((this.mInsets + params.topMargin) + child.getHeight()) + params.bottomMargin) + this.mInsets;
                    bottom = top + this.mDivider.getIntrinsicHeight();
                }
            }
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int left = (child.getRight() + ((RecyclerView.LayoutParams) child.getLayoutParams()).rightMargin) + this.mInsets;
            this.mDivider.setBounds(left, top, left + this.mDivider.getIntrinsicWidth(), bottom);
            this.mDivider.draw(c);
        }
    }

    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.set(this.mInsets, this.mInsets, this.mInsets, this.mInsets);
    }
}
