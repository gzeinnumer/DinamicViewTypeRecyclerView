package com.gzeinnumer.dinamicviewtyperecyclerview.lib.helper;

import android.view.View;

public interface BindViewHolderMultiType<T> {
    TypeViewItem getItemViewType(int position);
    void bind(View holder, T data, int position, int viewType);
}
