package com.hzy.paykeyboard;

/**
 * Created by huzongyao on 16/3/8.
 * 定义密码按键事件接口
 */
public interface IKeyEventListener {
    /**
     * 当按键按下时调用
     * @param keyCode {@link android.view.KeyEvent}
     * @param keyChar 数字按键字符串"1", "2" ... 删除键返回 "<"
     */
    void onKeyDown(int keyCode, String keyChar);
}
