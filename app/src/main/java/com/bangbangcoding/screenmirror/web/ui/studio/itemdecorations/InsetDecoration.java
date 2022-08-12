package com.bangbangcoding.screenmirror.web.ui.studio.itemdecorations;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bangbangcoding.screenmirror.R;


public class InsetDecoration extends RecyclerView.ItemDecoration {
    private int mInsets;

    public InsetDecoration(Context context) {
        this.mInsets = context.getResources().getDimensionPixelSize(R.dimen.card_insets);
    }

    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.set(this.mInsets, this.mInsets, this.mInsets, this.mInsets);
    }
}
