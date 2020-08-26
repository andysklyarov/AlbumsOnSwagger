package com.sklyarov.albumsonswagger;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int pixelOffset = view.getContext().getResources().getDimensionPixelOffset(R.dimen.li_margin);

        outRect.left = pixelOffset;
        outRect.right = pixelOffset;
        outRect.top = pixelOffset / 2;
        outRect.bottom = pixelOffset / 2;

    }
}
