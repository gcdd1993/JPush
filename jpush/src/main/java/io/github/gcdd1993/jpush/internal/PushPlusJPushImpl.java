package io.github.gcdd1993.jpush.internal;

import com.alibaba.fastjson.JSON;
import io.github.gcdd1993.jpush.JPushType;
import io.github.gcdd1993.jpush.PushResult;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author gcdd1993
 * @since 2020/12/23
 */
@Slf4j
class PushPlusJPushImpl
        extends AbstractJPushImpl {
    private static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * 用户令牌，可直接加到请求地址后，如：http://pushplus.hxtrip.com/send/{token}
     */
    private final String token;

    /**
     * 群组编码，不填仅发送给自己
     */
    private final String topic;

    /**
     * 发送模板，默认为html
     * <p>
     * html	默认模板，支持html文本
     * json	内容基于json格式展示
     * cloudMonitor	阿里云监控报警定制模板
     * jenkins	jenkins插件定制模板
     * route	路由器插件定制模板
     * 详见 <a href="https://pushplus.hxtrip.com/doc/guide/api.html"></a>
     */
    private final String template;

    PushPlusJPushImpl(Map<String, String> config) {
        super(config.getOrDefault("domain", "pushplus.hxtrip.com"));
        this.token = Objects.requireNonNull(config.get("token"), "找不到pushplus配置：token");
        this.topic = Objects.requireNonNull(config.get("topic"), "找不到pushplus配置：topic");
        this.template = Objects.requireNonNull(config.get("template"), "找不到pushplus配置：template");
        this.requestBuilder
                .addHeader("Content-Type", "application/json");
    }

    @SuppressWarnings("unchecked")
    @Override
    public PushResult push(String title, String content) {
        RequestData requestData = RequestData.builder()
                .token(token)
                .title(title)
                .content(content)
                .topic(topic)
                .template(template)
                .build();
        String pushUrl = "https://" + domain + "/send/";
        try {
            RequestBody requestBody = RequestBody.Companion.create(JSON.toJSONString(requestData), MEDIA_TYPE_JSON);
            Request request = this.requestBuilder
                    .url(pushUrl)
                    .method("POST", requestBody)
                    .build();
            OkHttpClient client = this.okHttpClientBuilder.build();
            Response response = client.newCall(request)
                    .execute();
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                Map<String, Object> res = JSON.parseObject(body, Map.class);
                if (Objects.equals(res.get("code"), 200)) {
                    return PushResult.success(body);
                } else {
                    return PushResult.fail((int) res.get("code"), (String) res.get("msg"));
                }
            } else {
                return PushResult.fail("调用PUSH+服务失败");
            }
        } catch (IOException ex) {
            log.error("cannot push to server. title {}, content {}", title, content, ex);
            return PushResult.fail("调用PUSH+服务失败");
        }
    }

    @Data
    @Builder
    private static class RequestData {
        private String token;
        private String title;
        private String content;
        private String topic;
        private String template;
    }
}
