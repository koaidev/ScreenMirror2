package com.bangbangcoding.screenmirror.web.ui.studio.adapter;

import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bangbangcoding.screenmirror.web.ui.studio.callback.OnRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

public abstract class ListAdapter<ViewHolderType extends RecyclerView.ViewHolder, ModelType> extends RecyclerView.Adapter<ViewHolderType> implements OnClickListener {
    private List<ModelType> mDataList = new ArrayList<>();
    private OnRecyclerViewItemClickListener<ModelType> mItemClickListener;
    protected OnItemActionsListener<ModelType> mItemsChangedListener;
    protected SparseBooleanArray mSelectedItems = new SparseBooleanArray();
    protected boolean mSelectionEnabled = false;

    public interface OnItemActionsListener<ModelType> {
        void onRemoveItem(ModelType modelType);
    }

    public abstract void bindView(ViewHolderType viewHolderType, ModelType modelType);

    @NonNull
    public abstract ViewHolderType onCreateViewHolder(@NonNull ViewGroup viewGroup, int i);

    public void setItemsChangedListener(OnItemActionsListener<ModelType> itemsChangedListener) {
        mItemsChangedListener = itemsChangedListener;
    }

    public void add(ModelType dataItem, int pos) {
        mDataList.add(pos, dataItem);
        notifyItemInserted(pos);
    }

    public void update(ModelType dataItem) {
        int position = mDataList.indexOf(dataItem);
        if (position != -1) {
            mDataList.set(position, dataItem);
            notifyItemChanged(position);
        }
    }

    public void update(ModelType dataItem, int position) {
        mDataList.set(position, dataItem);
        notifyItemChanged(position);
    }

    public void addAll(List<ModelType> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public void setSelectionEnabled(boolean selectionEnabled) {
        mSelectionEnabled = selectionEnabled;
    }

    public void remove(ModelType dataItem) {
        int pos = mDataList.indexOf(dataItem);
        if (mDataList.remove(dataItem)) {
            notifyItemRemoved(pos);
        }
    }

    public void toggleSelection(int pos) {
        if (mSelectedItems.get(pos, false)) {
            mSelectedItems.delete(pos);
        } else {
            mSelectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void setSelected(int pos) {
        mSelectedItems.put(pos, true);
        notifyItemChanged(pos);
    }

    public void clearSelection(int pos) {
        if (mSelectedItems.get(pos, false)) {
            mSelectedItems.delete(pos);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        if (mSelectedItems.size() > 0) {
            mSelectedItems.clear();
            notifyDataSetChanged();
        }
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    public ArrayList<ModelType> getSelectedItems() {
        ArrayList<ModelType> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++) {
            items.add(getItem(mSelectedItems.keyAt(i)));
        }
        return items;
    }

    private ModelType getItem(int pos) {
        return mDataList.get(pos);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderType holder, int position) {
        ModelType model = mDataList.get(position);
        bindView(holder, model);
        holder.itemView.setTag(model);
    }

    @Override
    public long getItemId(int position) {
        return (long) getDataList().get(position).hashCode();
    }

    private List<ModelType> getDataList() {
        return mDataList;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener != null) {
            ModelType model = (ModelType) view.getTag();
            mItemClickListener.onItemClick(view, model, mDataList.indexOf(model));
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener<ModelType> listener) {
        mItemClickListener = listener;
    }
}
