package com.static4u.netcar.activity.comment;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.static4u.netcar.R;
import com.static4u.netcar.model.CommentInfo;
import com.static4u.netcar.utils.CommonUtil;
import com.static4u.netcar.utils.image.ImageLoadUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private List<CommentInfo> data;
    private LayoutInflater lf;
    private int w;

    public CommentAdapter(Context context, List<CommentInfo> data) {
        this.context = context;
        this.data = data;
        this.lf = LayoutInflater.from(context);
        this.w = CommonUtil.getScreenWidth(context) - CommonUtil.dipTopx(context, 24) * 2;
    }

    public void update(List<CommentInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CommentInfo getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = lf.inflate(R.layout.item_comment, viewGroup, false);
            view.setTag(new CommentViewHolder(view));
        }
        initViews(getItem(i), (CommentViewHolder) view.getTag());
        return view;
    }

    private void initViews(final CommentInfo item, final CommentViewHolder holder) {
        ImageLoadUtil.loadImage(context, item.getHeader(), new ImageLoadUtil.OnImageSavedListener() {
            @Override
            public void onImageSavedSuccess(byte[] img) {
            }

            @Override
            public void onImageSavedSuccess(Bitmap img) {
                holder.ivHeader.setImageBitmap(ImageLoadUtil.createCircleImage(img));
                holder.tvHeaderTip.setVisibility(View.GONE);
            }

            @Override
            public void onImageSavedFailed() {
                holder.ivHeader.setImageResource(R.drawable.bg_header);
                holder.tvHeaderTip.setVisibility(View.VISIBLE);
            }
        });
        holder.tvCommentName.setText(item.getName());
        holder.tvTime.setText(item.getTime());
        holder.tvComment.setText(item.getContent());

    }

    static class CommentViewHolder {
        @Bind(R.id.iv_header)
        ImageView ivHeader;
        @Bind(R.id.tv_header_tip)
        TextView tvHeaderTip;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.tv_comment_name)
        TextView tvCommentName;
        @Bind(R.id.tv_comment)
        TextView tvComment;

        CommentViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
