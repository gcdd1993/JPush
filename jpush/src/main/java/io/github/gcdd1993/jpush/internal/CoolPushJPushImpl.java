package io.github.gcdd1993.jpush.internal;

import com.alibaba.fastjson.JSON;
import io.github.gcdd1993.jpush.JPushType;
import io.github.gcdd1993.jpush.PushResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * QQ酷推
 *
 * <a href="https://cp.xuthus.cc/"></a>
 *
 * @author gcdd1993
 * @since 2020/12/23
 */
@Slf4j
class CoolPushJPushImpl
        extends AbstractJPushImpl {
    // 0-domain, 1-mode, 2-sKey
    private static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * QQ酷推SKey
     */
    private final String sKey;

    /**
     * QQ酷推 mode
     * <p>
     * send - 私聊推送
     * group - 群消息推送
     * wx - 微信消息推送
     */
    private final String mode;

    /**
     * 默认mode是send，私聊推送
     */
    CoolPushJPushImpl(Map<String, String> config) {
        super(config.getOrDefault("domain", "push.xuthus.cc"));
        this.sKey = Objects.requireNonNull(config.get("sKey"), "找不到QQ酷推配置：sKey");
        this.mode = Objects.requireNonNull(config.get("mode"), "找不到QQ酷推配置：mode");
    }

    @SuppressWarnings("unchecked")
    @Override
    public PushResult push(String title, String content) {
        String pushUrl = "https://" + domain + "/" + mode + "/" + sKey;
        try {
            RequestBody requestBody = RequestBody.Companion.create(content, MEDIA_TYPE_JSON);
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
                return PushResult.fail("调用QQ酷推服务失败");
            }
        } catch (IOException ex) {
            log.error("cannot push to server. title {}, content {}", title, content, ex);
            return PushResult.fail("调用QQ酷推服务失败");
        }
    }
}
