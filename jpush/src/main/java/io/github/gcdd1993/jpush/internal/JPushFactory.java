package io.github.gcdd1993.jpush.internal;

import io.github.gcdd1993.jpush.JPush;
import io.github.gcdd1993.jpush.PushResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 构造工厂，创建JPush实例
 *
 * @author gcdd1993
 * @date 2020/12/23
 * @since 1.0.0
 */
@Slf4j
public class JPushFactory {
    private static final String WX_SERVER_DOMAIN = "sc.ftqq.com";
    private static final String PUSH_PLUS_DOMAIN = "pushplus.hxtrip.com";
    private static final String COOL_PUSH_DOMAIN = "push.xuthus.cc";
    private static final String IGOT_DOMAIN = "push.hellyw.com";

    private final Map<String, JPush> pushMap = new LinkedHashMap<>();

    /**
     * 推送消息至所有已添加的通道
     *
     * @param title   消息标题
     * @param content 消息内容
     * @return 消息推送结果
     */
    public Map<String, PushResult> push(String title, String content) {
        if (log.isDebugEnabled()) {
            log.info("prepare to push to {} jpush.", pushMap.size());
        }
        Map<String, PushResult> pushResultMap = new LinkedHashMap<>();
        pushMap
                .forEach((sign, jPush) -> pushResultMap.put(sign, jPush.push(title, content)));
        return pushResultMap;
    }

    public CompletableFuture<Map<String, PushResult>> pushAsync(String title, String content) {
        return CompletableFuture
                .supplyAsync(() -> push(title, content))
                .exceptionally(ex -> {
                    log.warn("caught error", ex);
                    return Collections.emptyMap(); // system error
                });
    }

    /**
     * 推送至指定通道
     *
     * @param title   消息标题
     * @param content 消息内容
     * @param sign    消息通道签名
     * @return 消息推送结果
     */
    public PushResult pushOnce(String title, String content, String sign) {
        if (!pushMap.containsKey(sign)) {
            log.warn("cannot find jPush instance by sign {}", sign);
            return PushResult.fail(MessageFormat.format("找不到指定的推送通道，推送通道的sign可能错误，sign --> {0}", sign));
        }
        return pushMap.get(sign).push(title, content);
    }

    public CompletableFuture<PushResult> pushOnceAsync(String title, String content, String sign) {
        return CompletableFuture
                .supplyAsync(() -> pushOnce(title, content, sign))
                .exceptionally(ex -> {
                    log.warn("caught error", ex);
                    return PushResult.fail(ex.getMessage());
                });
    }

    public String addWxServer(String scKey) {
        return _addJpush(scKey, new WxServerPushImpl(WX_SERVER_DOMAIN, scKey));
    }

    public String addPushPlus(String token, String topic) {
        return _addJpush(token + topic, new PushPlusJPushImpl(PUSH_PLUS_DOMAIN, token, topic));
    }

    public String addPushPlus(String token, String topic, String template) {
        return _addJpush(token + token + template, new PushPlusJPushImpl(PUSH_PLUS_DOMAIN, token, topic, template));
    }

    public String addCoolPush(String sKey) {
        return _addJpush(sKey, new CoolPushJPushImpl(COOL_PUSH_DOMAIN, sKey));
    }

    public String addCoolPush(String sKey, String mode) {
        return _addJpush(sKey + mode, new CoolPushJPushImpl(COOL_PUSH_DOMAIN, sKey, mode));
    }

    public String addIGot(String key, String topic) {
        return _addJpush(key + topic, new IGotJPushImpl(IGOT_DOMAIN, key, topic));
    }

    private String _addJpush(String sign, JPush jPush) {
        sign = DigestUtils.md5Hex(sign);
        if (pushMap.containsKey(sign)) {
            log.warn("jPush with sign {} is already exists.", sign);
        } else {
            pushMap.put(sign, jPush);
        }
        return sign;
    }

}
