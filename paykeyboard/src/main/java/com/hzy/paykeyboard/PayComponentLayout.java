package com.hzy.paykeyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by huzongyao on 16/3/4.
 * 该类负责密码输入组件的绘制，包括密码框和软键盘，不参与密码存储管理
 */
public class PayComponentLayout extends FrameLayout
        implements IKeyEventListener {

    private PasswordLayout mPasswordInputLayout;
    private KeyPanelLayout mPasswordPanel;
    private View mCancelButton;
    private View mForgetButton;
    private IKeyEventListener mKeyboardListener;

    private TextView mMainTitle;
    private TextView mSubTitle;

    public PayComponentLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PayComponentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PayComponentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initViews(context);
    }

    private void initViews(Context context) {
        inflaterViews(context);
    }

    private void inflaterViews(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.security_keyboard_layout, this);
        mPasswordInputLayout = (PasswordLayout) root.findViewById(R.id.password_input_layout);
        mPasswordPanel = (KeyPanelLayout) root.findViewById(R.id.password_key_panel);
        mCancelButton = root.findViewById(R.id.password_input_cancel);
        mForgetButton = root.findViewById(R.id.password_input_forget);
        mMainTitle = (TextView) root.findViewById(R.id.password_input_main_title);
        mSubTitle = (TextView) root.findViewById(R.id.password_input_sub_title);
        mPasswordPanel.setKeyboardListener(this);
    }

    public TextView getMainTitle() {
        return mMainTitle;
    }

    public TextView getSubTitle() {
        return mSubTitle;
    }

    /**
     * 一些子控件点击事件
     *
     * @param mOnclickListener
     */
    public void setOnComponentClickListener(OnClickListener mOnclickListener) {
        if (mOnclickListener != null) {
            mCancelButton.setOnClickListener(mOnclickListener);
            mForgetButton.setOnClickListener(mOnclickListener);
        }
    }

    public void clear() {
        mPasswordInputLayout.setCurrentPasswordLength(0);
    }

    public void setKeyboardListener(IKeyEventListener keyboardListener) {
        this.mKeyboardListener = keyboardListener;
    }

    public void setPasswordLength(int length) {
        mPasswordInputLayout.setPasswordLength(length);
    }

    @Override
    public void onKeyDown(int keyCode, String keyChar) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            mPasswordInputLayout.deletePasswordChar();
        } else {
            mPasswordInputLayout.addPasswordChar();
        }
        if (mKeyboardListener != null) {
            mKeyboardListener.onKeyDown(keyCode, keyChar);
        }
    }
}
