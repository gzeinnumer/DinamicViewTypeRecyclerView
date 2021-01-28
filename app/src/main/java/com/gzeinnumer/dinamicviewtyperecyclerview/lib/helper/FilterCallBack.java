package com.gzeinnumer.dinamicviewtyperecyclerview.lib.helper;

import java.util.List;

public interface FilterCallBack<T> {
    List<T> performFiltering(CharSequence constraint, List<T> listFilter);
}
