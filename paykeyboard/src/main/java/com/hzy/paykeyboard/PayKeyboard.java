package com.hzy.paykeyboard;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import java.util.Stack;

/**
 * Created by huzongyao on 16/3/7.
 * 该类负责密码输入键盘的显示以及事件处理，包括密码管理，存取
 */
public class PayKeyboard extends PopupWindow
        implements View.OnClickListener, IKeyEventListener, View.OnKeyListener {

    public static final int PASSWORD_MAX_LENGTH = 12;
    public static final int PASSWORD_MIN_LENGTH = 1;
    private final InputMethodManager mInputManager;

    private PayComponentLayout mWindowLayout;
    private Activity mActivity;
    private IKeyboardEncryptable mEncryptable;
    private IPasswordCallback mPasswordCallback;
    private Stack<String> mPasswordStack;
    private int mPasswordLength = PasswordLayout.DEFAULT_PASSWORD_LENGTH;

    public PayKeyboard(Activity activity) {
        this(activity, 0);
    }

    public PayKeyboard(Activity activity, int passWordLength) {
        this(activity, passWordLength, null);
    }

    public PayKeyboard(Activity activity, int passWordLength, IKeyboardEncryptable encryptable) {
        this.mActivity = activity;
        this.mEncryptable = encryptable;
        mPasswordStack = new Stack<>();
        mWindowLayout = new PayComponentLayout(mActivity);
        mInputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        setContentView(mWindowLayout);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.keyboard_anim);
        setPasswordLength(passWordLength);
        setFocusable(true);

        mWindowLayout.setFocusable(true);
        mWindowLayout.setFocusableInTouchMode(true);
        mWindowLayout.setOnComponentClickListener(this);
        mWindowLayout.setKeyboardListener(this);
        mWindowLayout.setOnKeyListener(this);
    }

    /**
     * 设置密码长度，会导致已输入的密码被清空
     *
     * @param length
     */
    public void setPasswordLength(int length) {
        if (length > PASSWORD_MIN_LENGTH && length < PASSWORD_MAX_LENGTH) {
            clear();
            mPasswordLength = length;
            mWindowLayout.setPasswordLength(length);
        }
    }

    /**
     * 指定加密算法，为空，则会调用内部加密算法
     * 加密算法改变，会导致已输入的密码被清空
     *
     * @param encryptable
     */
    public void setEncryptable(IKeyboardEncryptable encryptable) {
        clear();
        this.mEncryptable = encryptable;
    }

    /**
     * 设置回调接口
     *
     * @param callback
     */
    public void setPasswordCallback(IPasswordCallback callback) {
        this.mPasswordCallback = callback;
    }

    public void show(IPasswordCallback callback) {
        setPasswordCallback(callback);
        show();
    }

    /**
     * 显示安全键盘，如果有输入法软键盘在显示，则先关闭
     */
    public void show() {
        View parent = mActivity.findViewById(android.R.id.content);
        if (!isShowing() && parent != null) {
            mInputManager.hideSoftInputFromWindow(parent.getWindowToken(), 0);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }
    }

    public void setMainTitle(CharSequence text) {
        mWindowLayout.getMainTitle().setText(text);
    }

    public void setMainTitle(@StringRes int resid) {
        mWindowLayout.getMainTitle().setText(resid);
    }

    public void setSubTitle(CharSequence text) {
        mWindowLayout.getSubTitle().setText(text);
    }

    public void setSubTitle(@StringRes int resid) {
        mWindowLayout.getSubTitle().setText(resid);
    }

    /**
     * 清楚该会话缓存的密码以及UI组建还原
     */
    public void clear() {
        mPasswordStack.clear();
        mWindowLayout.clear();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        clear();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.password_input_cancel) {
            dismiss();
            if (mPasswordCallback != null) {
                mPasswordCallback.onInputCancel();
            }
        } else if (id == R.id.password_input_forget) {
            if (mPasswordCallback != null) {
                mPasswordCallback.onPasswordForget();
            }
        }
    }

    @Override
    public void onKeyDown(int keyCode, String keyChar) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (!mPasswordStack.empty()) {
                mPasswordStack.pop();
            }
        } else {
            if (mPasswordStack.size() < mPasswordLength) {
                mPasswordStack.push(InternalEncrpty.encryptKeyChar(keyChar));
            }
            if (mPasswordStack.size() >= mPasswordLength) {
                if (mPasswordCallback != null) {
                    mPasswordCallback.onInputComplete(getEncrypted());
                }
            }
        }
    }

    private Object getEncrypted() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mPasswordStack.size(); i++) {
            sb.append(mPasswordStack.get(i));
        }
        if (mEncryptable != null) {
            return mEncryptable.encrypt(InternalEncrpty.decryptString(sb.toString()));
        }
        return sb.toString();
    }

    /**
     * 获取解密后的原文
     *
     * @param object 加密后的秘文，加密后的密文可能是字符串也可能是byte[]或其它类型，所以用Object
     * @return 解密后的原文
     */
    public String getDecryptedPassword(Object object) {
        if (mEncryptable != null) {
            return mEncryptable.decrypt(object);
        }
        return InternalEncrpty.decryptString((String) object);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
            if (mPasswordCallback != null) {
                mPasswordCallback.onInputCancel();
            }
            return true;
        }
        return false;
    }

    /**
     * 内部加密类
     * 若使用者不指定具体加密算法，则只通过该算法加密
     */
    private static class InternalEncrpty {
        private static final char DEFAULT_XOR_CHAR = 0x11;

        public static String encryptKeyChar(String keyChar) {
            char key = keyChar.charAt(0);
            key ^= DEFAULT_XOR_CHAR;
            return String.valueOf(key);
        }

        public static String decryptString(String input) {
            char[] inputArray = input.toCharArray();
            for (int i = 0; i < inputArray.length; i++) {
                inputArray[i] ^= DEFAULT_XOR_CHAR;
            }
            return String.copyValueOf(inputArray);
        }
    }
}
