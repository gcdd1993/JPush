package io.github.gcdd1993.jpush.internal;

import io.github.gcdd1993.jpush.JPush;
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
    /**
     * Server酱域名
     */
    private static final String WX_SERVER_DOMAIN = "sc.ftqq.com";

    /**
     * push+域名
     */
    private static final String PUSH_PLUS_DOMAIN = "pushplus.hxtrip.com";

    private static final String COOL_PUSH_DOMAIN = "push.xuthus.cc";

    public static JPush buildWxServer(String scKey) {
        return new WxServerPushImpl(scKey, WX_SERVER_DOMAIN);
    }

    public static JPush buildPushPlus(String token, String topic) {
        return new PushPlusJPushImpl(token, topic, PUSH_PLUS_DOMAIN);
    }

    public static JPush buildPushPlus(String token, String topic, String template) {
        return new PushPlusJPushImpl(token, topic, PUSH_PLUS_DOMAIN, template);
    }

    public static JPush buildCoolPush(String sKey) {
        return new CoolPushJPushImpl(sKey, "send");
    }

    public static JPush buildCoolPush(String sKey, String mode) {
        return new CoolPushJPushImpl(sKey, mode);
    }

}
