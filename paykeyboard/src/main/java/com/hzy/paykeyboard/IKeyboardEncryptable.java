package com.hzy.paykeyboard;

/**
 * Created by huzongyao on 16/3/8.
 * 外部加密接口，指定字符串到Object的加密算法
 */
public interface IKeyboardEncryptable {
    /**
     * 用户自定义加密算法接口
     *
     * @param inString 输入密码
     * @return 加密后数据
     */
    Object encrypt(String inString);

    /**
     * 用户自定义解密接口
     *
     * @param encrypted 加密数据
     * @return 解密后密码串
     */
    String decrypt(Object encrypted);
}
