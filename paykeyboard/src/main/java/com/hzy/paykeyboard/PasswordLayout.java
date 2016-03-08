package com.hzy.paykeyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 该类负责显示方格状密码输入框：▣▣▣▣□□□
 */
public class PasswordLayout extends LinearLayout {
    public static final int DEFAULT_PASSWORD_LENGTH = 6;

    private Drawable mLineDrawable;
    private int mPasswordLength;
    private int mDividerColor;
    private int mDividerWidth;
    private AtomicInteger mCurrentLength;
    private View[] mDotViews;

    public PasswordLayout(Context context) {
        super(context);
        initViews(context);
        init(context, null, 0);
    }

    public PasswordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PasswordLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mCurrentLength = new AtomicInteger(0);
        initAttrs(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PasswordLayout, defStyleAttr, 0);
        mPasswordLength = ta.getInt(R.styleable.PasswordLayout_passwordLength, DEFAULT_PASSWORD_LENGTH);
        mDividerColor = ta.getInt(R.styleable.PasswordLayout_dividerColor, 0x00000000);
        mDividerWidth = (int) ta.getDimension(R.styleable.PasswordLayout_dividerWidth, 1);
        ta.recycle();
    }

    private void initViews(Context context) {
        mLineDrawable = new ColorDrawable(mDividerColor);
        mDotViews = new View[mPasswordLength];
        setShowDividers(SHOW_DIVIDER_NONE);
        setOrientation(HORIZONTAL);
        inflaterViews(context);
    }

    private void inflaterViews(Context context) {
        for (int i = 0; i < mPasswordLength; i++) {
            if (i != 0) {
                View dividerView = new View(context);
                LayoutParams dividerParams = new LayoutParams(mDividerWidth, LayoutParams.MATCH_PARENT);
                dividerView.setBackgroundDrawable(mLineDrawable);
                addView(dividerView, dividerParams);
            }
            View dotView = LayoutInflater.from(context).inflate(R.layout.password_input_layout, null);
            mDotViews[i] = dotView;
            dotView.setVisibility(View.INVISIBLE);
            LayoutParams dotParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
            addView(dotView, dotParams);
        }
    }

    /**
     * 设置能输入的最长密码
     *
     * @param length
     */
    public void setPasswordLength(int length) {
        removeAllViews();
        mPasswordLength = length;
        initViews(getContext());
    }

    public int getPasswordLength() {
        return mPasswordLength;
    }

    /**
     * 设置当前已输入密码长度
     *
     * @param length
     */
    public void setCurrentPasswordLength(int length) {
        mCurrentLength.set(length);
        for (int i = 0; i < mPasswordLength; i++) {
            mDotViews[i].setVisibility(i < mCurrentLength.get() ? VISIBLE : INVISIBLE);
        }
    }

    public int getCurrentPasswordLength() {
        return mCurrentLength.get();
    }

    /**
     * 输入一位密码
     */
    public void addPasswordChar() {
        if (mCurrentLength.get() < mPasswordLength) {
            mDotViews[mCurrentLength.getAndIncrement()].setVisibility(View.VISIBLE);
        }
    }

    /**
     * 删除一位密码
     */
    public void deletePasswordChar() {
        if (mCurrentLength.get() > 0) {
            mDotViews[mCurrentLength.decrementAndGet()].setVisibility(View.INVISIBLE);
        }
    }
}
