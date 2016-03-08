package com.hzy.paykeyboard;

import android.content.Context;
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

    public static final String[] DIGIT_KEYS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "<"};
    public static final int EMPTY_INDEX = 9;
    public static final int BACK_INDEX = 11;
    public static final int NUMBER_MAX = 9;
    public static final int NUMBER_MIN = 0;

    private IKeyEventListener mKeyboardListener;

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
        buttonText.setText(DIGIT_KEYS[index]);
        viewGroup.setTag(DIGIT_KEYS[index]);
        viewGroup.setOnClickListener(this);

        if (index == EMPTY_INDEX || index == BACK_INDEX) {
            viewGroup.setBackgroundResource(R.drawable.keyboard_gray_button);
            if (index == EMPTY_INDEX) {
                viewGroup.setClickable(false);
                viewGroup.setEnabled(false);
            } else {
                buttonText.setVisibility(View.GONE);
                buttonImage.setImageResource(R.drawable.safe_keyboard_ic_delete);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mKeyboardListener != null) {
            String code = (String) v.getTag();
            if (code.equals("<")) {
                mKeyboardListener.onKeyDown(KeyEvent.KEYCODE_DEL, code);
            } else {
                try {
                    int num = Integer.parseInt(code);
                    if (num >= NUMBER_MIN && num <= NUMBER_MAX) {
                        mKeyboardListener.onKeyDown(KeyEvent.KEYCODE_0 + num, code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setKeyboardListener(IKeyEventListener keyboardListener) {
        this.mKeyboardListener = keyboardListener;
    }
}
