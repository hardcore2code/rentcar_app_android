package com.static4u.netcar.activity.home;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.static4u.netcar.R;
import com.static4u.netcar.activity.detail.CarDetailActivity;
import com.static4u.netcar.base.BaseActivity;
import com.static4u.netcar.constant.GlobalConstant;
import com.static4u.netcar.model.CarInfo;
import com.static4u.netcar.utils.CommonUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeAdapter extends BaseAdapter {
    private Context context;
    private List<CarInfo> data;
    private LayoutInflater lf;
    private int w;

    public HomeAdapter(Context context, List<CarInfo> data) {
        this.context = context;
        this.data = data;
        this.lf = LayoutInflater.from(context);
        this.w = CommonUtil.getScreenWidth(context) - CommonUtil.dipTopx(context, 24) * 2;
    }

    public void update(List<CarInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CarInfo getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = lf.inflate(R.layout.item_car, viewGroup, false);
            view.setTag(new CarViewHolder(view));
        }
        initViews(getItem(i), (CarViewHolder) view.getTag());
        return view;
    }

    private void initViews(final CarInfo item, CarViewHolder holder) {
        holder.tvType.setText(item.getType());
        holder.tvBrand.setText(item.getBrand());
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("¥" + item.getPrice());
        holder.tvPriceOld.setText("¥" + item.getPriceOld());
        CommonUtil.addMidLine(holder.tvPriceOld);
        holder.tvSub.setText(item.getSub());

        holder.vpCar.setAdapter(new ImagePagerAdapter(context, item.getImgList()));
        holder.vpCar.setOnPageChangeListener(new ImagePageChangeListener(context, holder.llIndex, item));
        holder.vpCar.setCurrentItem(item.getpIndex());
        holder.vpCar.setLayoutParams(new RelativeLayout.LayoutParams(w, w * GlobalConstant.IMG_HEIGHT / GlobalConstant.IMG_WIDTH));

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarDetailActivity.startDetail((BaseActivity) context, item);
            }
        });
    }

    static class CarViewHolder {
        @Bind(R.id.cv)
        CardView cv;

        @Bind(R.id.vp_car)
        ViewPager vpCar;
        @Bind(R.id.ll_index)
        LinearLayout llIndex;
        @Bind(R.id.tv_type)
        TextView tvType;
        @Bind(R.id.tv_brand)
        TextView tvBrand;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_price)
        TextView tvPrice;
        @Bind(R.id.tv_price_old)
        TextView tvPriceOld;
        @Bind(R.id.tv_sub)
        TextView tvSub;

        CarViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
