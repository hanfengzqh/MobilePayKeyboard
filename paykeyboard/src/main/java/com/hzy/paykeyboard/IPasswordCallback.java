package com.hzy.paykeyboard;

/**
 * Created by huzongyao on 16/3/8.
 */
public interface IPasswordCallback {

    /**
     * 密码输入完成时回调
     *
     * @param encrypted 密文
     */
    void onInputComplete(Object encrypted);

    /**
     * 当用户取消密码输入时回调
     */
    void onInputCancel();

    /**
     * 当用户点击忘记密码
     */
    void onPasswordForget();
}
