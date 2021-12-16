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
 * IGot聚合推送
 *
 * <a href="https://github.com/wahao/Bark-MP-helper"></a>
 *
 * @author gcdd1993
 * @since 2020/12/23
 */
@Slf4j
class IGotJPushImpl
        extends AbstractJPushImpl {
    private static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");

    private final String key;
    private final String topic;

    IGotJPushImpl(Map<String, String> config) {
        super(config.getOrDefault("domain", "push.hellyw.com"));
        this.key = Objects.requireNonNull(config.get("key"), "找不到IGot聚合推送配置：key");
        this.topic = Objects.requireNonNull(config.get("topic"), "找不到IGot聚合推送配置：topic");
    }

    @SuppressWarnings("unchecked")
    @Override
    public PushResult push(String title, String content) {
        String pushUrl = "https://" + domain + "/" + key;
        try {
            RequestData requestData = RequestData.builder()
                    .title(title)
                    .content(content)
                    .topic(topic)
                    .build();
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
                if (Objects.equals(res.get("ret"), 0)) {
                    return PushResult.success(body);
                } else {
                    return PushResult.fail((int) res.get("ret"), (String) res.get("errMsg"));
                }
            } else {
                return PushResult.fail("调用IGot服务失败");
            }
        } catch (IOException ex) {
            log.error("cannot push to server. title {}, content {}", title, content, ex);
            return PushResult.fail("调用IGot服务失败");
        }
    }

    @Data
    @Builder
    private static class RequestData {
        private String title;
        private String content;
        // 推送携带的url
        private String url;
        // 自动复制； 为1自动复制； 默认为1
        private String automaticallyCopy;
        // 需要复制的文本内容
        private String copy;
        // 目前仅在订阅消息下有效； 订阅者可通过推送主体选择是否接收消息
        private String topic;
        // 其余参数， 其他请求参数会作为调试参数以json的形式显示在推送内容中。
    }
}
