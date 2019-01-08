package com.static4u.netcar.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.static4u.netcar.R;

public class MyEditText extends EditText {
    public enum EditTextType {
        EDIT_WITH_DELETE,   // 带删除按钮
        EDIT_WITH_PEN,      // 带编辑图标
        EDIT_WITH_NONE      // 无图标
    }

    public EditTextType getType() {
        return type;
    }

    public void setType(EditTextType type) {
        this.type = type;
    }

    private EditTextType type = EditTextType.EDIT_WITH_DELETE;
    private final static String TAG = "EditTextWithDel";
    private Drawable imgAble;
    private Context mContext;

    public MyEditText(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        setAttributeSet(attrs);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setAttributeSet(attrs);
        init();
    }

    protected void setAttributeSet(AttributeSet attributeSet) {
        final TypedArray a = mContext.obtainStyledAttributes(attributeSet, R.styleable.MyEditText);

        int index = a.getInt(R.styleable.MyEditText_met_type, 0);
        switch (index) {
            case 1:
                type = EditTextType.EDIT_WITH_PEN;
                imgAble = mContext.getResources().getDrawable(R.drawable.edit_pen);
                break;
            case 2:
                type = EditTextType.EDIT_WITH_NONE;
                imgAble = null;
                break;
            default:
                type = EditTextType.EDIT_WITH_DELETE;
                imgAble = mContext.getResources().getDrawable(R.drawable.edit_clear);
                break;
        }
    }

    private void init() {
        // 设置EditText的属性
        setMinHeight(mContext.getResources().getDimensionPixelSize(R.dimen.min_height));
        int left = mContext.getResources().getDimensionPixelSize(R.dimen.padding_left);
        int top = mContext.getResources().getDimensionPixelSize(R.dimen.padding_top);
        int right = mContext.getResources().getDimensionPixelSize(R.dimen.padding_right);
        int bottom = mContext.getResources().getDimensionPixelSize(R.dimen.padding_bottom);
        setPadding(left, top, right, bottom);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });

        setDrawable();
    }

    // 设置删除图片
    private void setDrawable() {
        if (length() < 1) {
            if (type == EditTextType.EDIT_WITH_DELETE) {
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        } else {
            if (isEnabled()) {
                setCompoundDrawablesWithIntrinsicBounds(null, null, imgAble, null);
            } else {
                if (type == EditTextType.EDIT_WITH_DELETE) {
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
        }
    }

    public void hideRightBtn() {
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            if (imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
                int eventX = (int) event.getRawX();
                int eventY = (int) event.getRawY();
                Rect rect = new Rect();
                getGlobalVisibleRect(rect);
                rect.left = rect.right - 50;
                if (rect.contains(eventX, eventY)) {
                    if (type == EditTextType.EDIT_WITH_DELETE) {
                        setText("");
                    } else if (type == EditTextType.EDIT_WITH_PEN) {
                        // 弹出键盘
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(MyEditText.this, 0);
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

}
