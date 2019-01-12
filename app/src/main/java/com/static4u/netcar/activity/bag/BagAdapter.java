package com.static4u.netcar.activity.bag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.static4u.netcar.R;
import com.static4u.netcar.model.BagInfo;
import com.static4u.netcar.utils.CommonUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BagAdapter extends BaseAdapter {
    private Context context;
    private List<BagInfo> data;
    private LayoutInflater lf;
    private int w;

    public BagAdapter(Context context, List<BagInfo> data) {
        this.context = context;
        this.data = data;
        this.lf = LayoutInflater.from(context);
        this.w = CommonUtil.getScreenWidth(context) - CommonUtil.dipTopx(context, 24) * 2;
    }

    public void update(List<BagInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public BagInfo getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = lf.inflate(R.layout.item_bag, viewGroup, false);
            view.setTag(new BagViewHolder(view));
        }
        initViews(getItem(i), (BagViewHolder) view.getTag());
        return view;
    }

    private void initViews(final BagInfo item, BagViewHolder holder) {
        holder.tvName.setText(item.getName());
        holder.tvIntro.setText(item.getIntro());
        holder.tvPrice.setText("计时费¥" + item.getPrice());
        holder.tvDeposit.setText("保证金" + item.getDeposit() + "元");
    }

    static class BagViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_intro)
        TextView tvIntro;
        @Bind(R.id.tv_deposit)
        TextView tvDeposit;
        @Bind(R.id.tv_price)
        TextView tvPrice;

        BagViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
