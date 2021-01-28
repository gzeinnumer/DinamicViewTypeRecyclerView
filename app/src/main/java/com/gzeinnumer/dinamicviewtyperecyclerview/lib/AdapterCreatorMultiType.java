package com.gzeinnumer.dinamicviewtyperecyclerview.lib;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.gzeinnumer.dinamicviewtyperecyclerview.R;
import com.gzeinnumer.dinamicviewtyperecyclerview.databinding.DefaultItemRvBinding;
import com.gzeinnumer.dinamicviewtyperecyclerview.lib.helper.TypeViewItem;
import com.gzeinnumer.dinamicviewtyperecyclerview.lib.helper.BindViewHolderMultiType;
import com.gzeinnumer.dinamicviewtyperecyclerview.lib.helper.FilterCallBack;
import com.gzeinnumer.dinamicviewtyperecyclerview.lib.helper.MyDiffUtilsCallBack;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class AdapterCreatorMultiType<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public static final String TAG = "Adapter_Creator";
    private List<T> list;
    private List<T> listFilter;
    private List<T> listReal;
    private List<TypeViewItem> typeViewItem;

    private int emptyLayout = -1;
    private int divider = -1;
    private int animation = -1;
    private boolean diffutils = true;
    private BindViewHolderMultiType<T> bindViewHolderMultiType;
    private FilterCallBack<T> filterCallBack;

    public void setEmptyLayout(int emptyLayout) {
        this.emptyLayout = emptyLayout;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint.length() > 0) {
                List<T> data = filterCallBack.performFiltering(constraint, listFilter);
                results.values = data;
                return results;
            } else {
                results.values = listReal;
                return results;
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public void setAnimation(int animation) {
        this.animation = animation;
    }

    public AdapterCreatorMultiType() {
        this.list = new ArrayList<>();
        this.listFilter = new ArrayList<>();
        this.listReal = new ArrayList<>();
        this.typeViewItem = new ArrayList<>();
    }

    private static final int TYPE_EMPTY = -1;

    @Override
    public int getItemViewType(int position) {
        if (list.size() == 0) {
            return TYPE_EMPTY;
        } else {
            typeViewItem.add(bindViewHolderMultiType.getItemViewType(position));
            return bindViewHolderMultiType.getItemViewType(position).getType();
        }
    }

    public void setDivider(int divider) {
        this.divider = divider;
    }

    public void setBindViewHolderMultiType(BindViewHolderMultiType<T> bindViewHolderMultiType) {
        this.bindViewHolderMultiType = bindViewHolderMultiType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return new ViewHolderEmpty(LayoutInflater.from(parent.getContext()).inflate(emptyLayout == -1 ? R.layout.default_empty_item : emptyLayout, parent, false));
        }
        else {
            DefaultItemRvBinding defaultItemRvBinding = DefaultItemRvBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            for (int i = 0; i < typeViewItem.size(); i++) {
                if (viewType == typeViewItem.get(i).getType()) {
                    ViewStub stub = defaultItemRvBinding.layoutStub;
                    stub.setLayoutResource(typeViewItem.get(i).getLayout());
                    View inflated = stub.inflate();
                    if (divider != -1) {
                        ViewStub stubDiv = defaultItemRvBinding.layoutDivider;
                        stubDiv.setLayoutResource(divider);
                        View inflatedDiv = stubDiv.inflate();
                    }
                    break;
                }
            }

            return new MyHolder<T>(defaultItemRvBinding, viewType);
        }
    }

    public void setEnableDiffUtils(boolean enable) {
        this.diffutils = enable;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (animation != -1) {
            holder.itemView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), animation));
        }
        if (holder.getItemViewType() != TYPE_EMPTY) {
            if (list.size() > 0) {
                ((MyHolder<T>) holder).bind(list.get(position), bindViewHolderMultiType, list.size(), divider);
            }
        }
    }

    public static class ViewHolderEmpty extends RecyclerView.ViewHolder {
        public ViewHolderEmpty(@NonNull View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() > 0 ? list.size() : 1;
    }

    public void setList(List<T> d) {
        if (diffutils) {
            if (this.list.size() == 0) {
                MyDiffUtilsCallBack<T> diffUtilsCallBack = new MyDiffUtilsCallBack<T>(d, list);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilsCallBack);

                list.addAll(d);
                listFilter.addAll(d);
                listReal.addAll(d);
                diffResult.dispatchUpdatesTo(this);
            } else {
                MyDiffUtilsCallBack<T> diffUtilsCallBack = new MyDiffUtilsCallBack<T>(list, d);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilsCallBack);

                list.clear();
                listFilter.clear();
                listReal.clear();
                list.addAll(d);
                listFilter.addAll(d);
                listReal.addAll(d);
                diffResult.dispatchUpdatesTo(this);
            }
        } else {
            this.list = new ArrayList<>(d);
            this.listFilter = new ArrayList<>(d);
            this.listReal = new ArrayList<>(d);
            notifyDataSetChanged();
        }
    }

    public AdapterCreatorMultiType<T> onFilter(FilterCallBack<T> filterCallBack) {
        this.filterCallBack = filterCallBack;
        return this;
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    public static class MyHolder<T> extends RecyclerView.ViewHolder {
        DefaultItemRvBinding itemRvBinding;
        View view;
        int viewType;

        public MyHolder(@NonNull DefaultItemRvBinding itemView, int viewType) {
            super(itemView.getRoot());
            view = itemView.getRoot();
            itemRvBinding = itemView;
            this.viewType = viewType;
        }

        public void bind(T data, BindViewHolderMultiType<T> bindViewHolderMultiType, int size, int divider) {
            if (divider != -1) {
                if (getAdapterPosition() == size - 1) {
                    itemRvBinding.layoutDivider.setVisibility(View.GONE);
                } else {
                    itemRvBinding.layoutDivider.setVisibility(View.VISIBLE);
                }
            }
            bindViewHolderMultiType.bind(view, data, getAdapterPosition(), viewType);
        }
    }
}
