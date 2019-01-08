package com.static4u.netcar.activity.home;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.static4u.netcar.R;
import com.static4u.netcar.model.CarInfo;
import com.static4u.netcar.utils.CommonUtil;
import com.static4u.netcar.utils.SLog;

public class ImagePageChangeListener implements ViewPager.OnPageChangeListener {

    private int pIndex, count;
    private CarInfo info;
    private LinearLayout ll;
    private LinearLayout.LayoutParams paramsDefault, paramsSelected;

    public ImagePageChangeListener(Context context, LinearLayout ll, CarInfo info) {
        this.info = info;
        this.ll = ll;
        this.count = info.getImgList().size();

        ll.removeAllViews();
        if (count > 1) {
            int w = CommonUtil.dipTopx(context, 4);
            paramsDefault = new LinearLayout.LayoutParams(w, w);
            paramsDefault.rightMargin = w;
            paramsSelected = new LinearLayout.LayoutParams(w * 2, w);
            paramsSelected.rightMargin = w;

            for (int i = 0; i < count; i++) {
                View dot = new View(context);
                dot.setLayoutParams(i == info.getpIndex() ? paramsSelected : paramsDefault);
                dot.setBackgroundResource(i == info.getpIndex() ? R.drawable.dot_green : R.drawable.dot_gray);
                ll.addView(dot);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (count > 1) {
            SLog.e(pIndex + " ---- " + count);

            View pView = ll.getChildAt(pIndex % count);
            pView.setBackgroundResource(R.drawable.dot_gray);
            pView.setLayoutParams(paramsDefault);

            pIndex = position;
            View pViewNow = ll.getChildAt(pIndex % count);
            pViewNow.setBackgroundResource(R.drawable.dot_green);
            pViewNow.setLayoutParams(paramsSelected);

            info.setpIndex(pIndex % count);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
