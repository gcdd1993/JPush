package io.github.gcdd1993.jpush.internal;

import com.alibaba.fastjson.JSON;
import io.github.gcdd1993.jpush.PushResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Server酱推送
 *
 * @author gcdd1993
 * @since 2020/12/23
 */
@Slf4j
class WxServerPushImpl
        extends AbstractJPushImpl {
    /**
     * Server酱推送SCKEY，详情请查看
     * <a href="http://sc.ftqq.com/?c=code"></a>获取Server酱SCKEY
     */
    private final String scKey;

    WxServerPushImpl(Map<String, String> config) {
        super(config.getOrDefault("domain", "sctapi.ftqq.com"));
        this.scKey = Objects.requireNonNull(config.get("scKey"), "找不到Server酱配置：scKey");
    }

    @SuppressWarnings("unchecked")
    @Override
    public PushResult push(String title, String content) {
        String pushUrl = "https://" + domain + "/" + scKey + ".send";
        try {
            FormBody formBody = new FormBody.Builder()
                    .add("text", title)
                    .add("desp", content)
                    .add("channel", "9")
                    .build();
            Request request = this.requestBuilder
                    .url(pushUrl)
                    .method("POST", formBody)
                    .build();
            OkHttpClient client = this.okHttpClientBuilder.build();
            Response response = client.newCall(request)
                    .execute();
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                Map<String, Object> res = JSON.parseObject(body, Map.class);
                if (Objects.equals(res.get("code"), 0)) {
                    return PushResult.success(body);
                } else {
                    return PushResult.fail((int) res.get("errno"), (String) res.get("errmsg"));
                }
            } else {
                return PushResult.fail("调用Server酱服务失败");
            }
        } catch (IOException ex) {
            log.error("cannot push to server. title {}, content {}", title, content, ex);
            return PushResult.fail("调用Server酱服务失败");
        }
    }

}
