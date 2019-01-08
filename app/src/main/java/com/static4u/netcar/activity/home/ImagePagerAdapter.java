package com.static4u.netcar.activity.home;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.static4u.netcar.R;
import com.static4u.netcar.utils.CommonUtil;
import com.static4u.netcar.utils.ImageLoadUtil;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private int width = 16, height = 9;
    private int rw, rh;

    private int size;
    private int mChildCount;
    private Context context;
    private List<String> list;

    ImagePagerAdapter(Context context, List<String> list) {
        this.rw = CommonUtil.getScreenWidth(context) - CommonUtil.dipTopx(context, context.getResources().getDimension(R.dimen.activity_horizontal_margin) * 2);
        this.rh = rw * height / width;

        this.context = context;
        this.list = list;

        if (list.size() < 1) {
            list.add("");
        }
        this.size = list.size();
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = size;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        if (size == 1) {
            return 1;
        } else if (size == 0) {
            return 0;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
        ((View) arg2).destroyDrawingCache();
    }

    @Override
    public Object instantiateItem(View view, int position) {
        View v = getPickupInfo(position % size);
        ((ViewPager) view).addView(v);
        return v;
    }

    private View getPickupInfo(final int position) {
        ImageView v = new ImageView(context);
        final String item = list.get(position);
        v.setLayoutParams(new ViewGroup.LayoutParams(rw, rh));
        v.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ImageLoadUtil.loadImage(context, item, v, R.drawable.ic_empty);
        return v;
    }
}
