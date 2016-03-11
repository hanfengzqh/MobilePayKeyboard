package com.hzy.paykeyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by huzongyao on 16/3/4.
 * 该类负责显示一个数字软键盘布局
 */
public class KeyPanelLayout extends FrameLayout implements View.OnClickListener {

    public static final String[] DIGIT_KEYS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "<"};
    public static final int NUMBER_MAX = 9;
    public static final int NUMBER_MIN = 0;

    private IKeyEventListener mKeyboardListener;
    private boolean mDotEnabled = false;

    public KeyPanelLayout(Context context) {
        super(context);
        initViews(context);
        init(context, null, 0);
    }

    public KeyPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public KeyPanelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initAttrs(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KeyPanelLayout, defStyleAttr, 0);
        mDotEnabled = ta.getBoolean(R.styleable.KeyPanelLayout_dotEnable, mDotEnabled);
        ta.recycle();
    }

    private void initViews(Context context) {
        inflaterViews(context);
    }

    private void inflaterViews(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        GridLayout panel = (GridLayout) inflater.inflate(R.layout.keyboard_panel_layout, null);
        addView(panel);
        int buttonCount = DIGIT_KEYS.length;
        for (int i = 0; i < buttonCount; i++) {
            ViewGroup root = (ViewGroup) inflater.inflate(R.layout.keyboard_key_layout, panel);
            initButtonView((ViewGroup) root.getChildAt(i), i);
        }
    }

    private void initButtonView(ViewGroup viewGroup, int index) {
        TextView buttonText = (TextView) viewGroup.findViewById(R.id.button_text);
        ImageView buttonImage = (ImageView) viewGroup.findViewById(R.id.button_image);
        String keyText = DIGIT_KEYS[index];
        viewGroup.setTag(keyText);
        viewGroup.setOnClickListener(this);
        if (keyText.equals("<")) {
            viewGroup.setBackgroundResource(R.drawable.keyboard_gray_button);
            buttonImage.setImageResource(R.drawable.safe_keyboard_ic_delete);
            return;
        }
        if (keyText.equals(".")) {
            viewGroup.setBackgroundResource(R.drawable.keyboard_gray_button);
            if (!mDotEnabled) {
                viewGroup.setClickable(false);
                viewGroup.setEnabled(false);
                return;
            }
        }
        buttonText.setText(keyText);
    }

    @Override
    public void onClick(View v) {
        if (mKeyboardListener != null) {
            String keyCode = (String) v.getTag();
            int eventCode = KeyEvent.KEYCODE_UNKNOWN;
            if (keyCode.equals("<")) {
                eventCode = KeyEvent.KEYCODE_DEL;
            } else if (keyCode.equals(".")) {
                eventCode = KeyEvent.KEYCODE_NUMPAD_DOT;
            } else {
                try {
                    int num = Integer.parseInt(keyCode);
                    if (num >= NUMBER_MIN && num <= NUMBER_MAX) {
                        eventCode = KeyEvent.KEYCODE_NUMPAD_0 + num;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mKeyboardListener.onKeyDown(eventCode, keyCode);
        }
    }

    public void setKeyboardListener(IKeyEventListener keyboardListener) {
        this.mKeyboardListener = keyboardListener;
    }
}
