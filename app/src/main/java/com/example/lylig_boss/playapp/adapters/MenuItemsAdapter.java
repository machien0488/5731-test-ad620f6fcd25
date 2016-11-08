package com.example.lylig_boss.playapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.models.ItemMenu;

import java.util.List;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 20/07/2016.
 */
public class MenuItemsAdapter extends RecyclerView.Adapter<MenuItemsAdapter.ViewHolder> {
    private final Context mContext;
    private final List<ItemMenu> mItemMenus;
    private OnItemClickListener mOnItemClickListener;

    public MenuItemsAdapter(Context context, List<ItemMenu> itemMenus) {
        mContext = context;
        mItemMenus = itemMenus;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClicklistener) {
        mOnItemClickListener = onItemClicklistener;
    }

    public interface OnItemClickListener {
        void Onclick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_navigation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ItemMenu item = mItemMenus.get(position);
        holder.mTvMenuItem.setText(item.getItemMenu());
        holder.mImgMenuItem.setImageResource(item.getIdImgMenu());
    }

    @Override
    public int getItemCount() {
        return mItemMenus != null ? mItemMenus.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {
        private final TextView mTvMenuItem;
        private final ImageView mImgMenuItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvMenuItem = (TextView) itemView.findViewById(R.id.tvNavItem);
            mImgMenuItem = (ImageView) itemView.findViewById(R.id.imgNavItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.Onclick(v, getAdapterPosition());
            }
        }
    }
}
