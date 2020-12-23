package io.github.gcdd1993.jpush.internal;

import com.alibaba.fastjson.JSON;
import io.github.gcdd1993.jpush.PushResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;

/**
 * @author gcdd1993
 * @date 2020/12/23
 * @since 1.0.0
 */
@Slf4j
public class PushPlusJPushImpl
        extends AbstractJPushImpl {
    private static final String DEFAULT_URL = "https://{0}/send/";
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

    PushPlusJPushImpl(String token, String topic, String domain) {
        this(token, topic, domain, "html");
    }

    PushPlusJPushImpl(String token, String topic, String domain, String template) {
        super(domain, null);
        this.token = token;
        this.topic = topic;
        this.template = template;
        this.requestBuilder
                .addHeader("Content-Type", "application/json");
    }

    @SuppressWarnings("unchecked")
    @Override
    public PushResult push(String title, String content) {
        RequestData requestData = new RequestData(
                token,
                title,
                content,
                topic,
                template
        );
        String pushUrl = MessageFormat.format(
                DEFAULT_URL,
                domain
        );
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

    @RequiredArgsConstructor
    @Getter
    @Setter
    private class RequestData {
        private final String token;
        private final String title;
        private final String content;
        private final String topic;
        private final String template;
    }
}
