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
 * IGot聚合推送
 *
 * <a href="https://github.com/wahao/Bark-MP-helper"></a>
 *
 * @author gcdd1993
 * @date 2020/12/23
 * @since 1.0.0
 */
@Slf4j
public class IGotJPushImpl
        extends AbstractJPushImpl {
    // 0-domain, 1-key
    private static final String DEFAULT_URL = "https://{0}/{1}";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");

    private final String key;
    private final String topic;

    IGotJPushImpl(String domain, String key, String topic) {
        super(domain);
        this.key = key;
        this.topic = topic;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PushResult push(String title, String content) {
        String pushUrl = MessageFormat.format(
                DEFAULT_URL,
                domain,
                key
        );
        try {
            RequestData requestData = new RequestData(title, content, topic);
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

    @RequiredArgsConstructor
    @Getter
    @Setter
    private class RequestData {
        public RequestData(String title, String content, String topic) {
            this(title, content, null, null, null, topic);
        }

        private final String title;
        private final String content;
        // 推送携带的url
        private final String url;
        // 自动复制； 为1自动复制； 默认为1
        private final String automaticallyCopy;
        // 需要复制的文本内容
        private final String copy;
        // 目前仅在订阅消息下有效； 订阅者可通过推送主体选择是否接收消息
        private final String topic;
        // 其余参数， 其他请求参数会作为调试参数以json的形式显示在推送内容中。
    }
}
