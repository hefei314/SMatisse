package com.hefei.smatisse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author: hefei
 *     time  : 2020/10/15
 *     desc  :
 * </pre>
 */
public class MultimediaListAdapter extends RecyclerView.Adapter<MultimediaListAdapter.MultimediaListViewHolder> {

    private Context mContext;

    private List<MultimediaListBean> mData = new ArrayList<>();

    public MultimediaListAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public MultimediaListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_multimedia_list, parent, false);
        return new MultimediaListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultimediaListViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mData.get(position).getPath())
                .centerCrop()
                .into(holder.ivMultimedia);
        holder.ivPlay.setVisibility(mData.get(position).getType() == 2 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<MultimediaListBean> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public static class MultimediaListViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivMultimedia;
        private ImageView ivPlay;

        public MultimediaListViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMultimedia = itemView.findViewById(R.id.iv_multimedia);
            ivPlay = itemView.findViewById(R.id.iv_play);
        }
    }

    public static class MultimediaListBean {
        // 1 图片 2 视频
        private int type;
        private String path;

        public MultimediaListBean(int type, String path) {
            this.type = type;
            this.path = path;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

}
