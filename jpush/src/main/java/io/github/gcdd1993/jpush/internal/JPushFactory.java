package io.github.gcdd1993.jpush.internal;

import io.github.gcdd1993.jpush.JPush;
import io.github.gcdd1993.jpush.internal.WxServerPushImpl;
import lombok.experimental.UtilityClass;

/**
 * 构造工厂，创建JPush实例
 *
 * @author gcdd1993
 * @date 2020/12/23
 * @since 1.0.0
 */
@UtilityClass
public class JPushFactory {

    public static JPush buildWxServer(String scKey) {
        return new WxServerPushImpl("SCU138193T2b2331d7d6b3da86610a44e6a49a4de65fe2bf9c86265", "sc.ftqq.com");
    }
}
