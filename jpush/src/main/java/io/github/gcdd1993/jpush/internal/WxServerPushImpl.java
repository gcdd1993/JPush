package io.github.gcdd1993.jpush.internal;

import com.alibaba.fastjson.JSON;
import io.github.gcdd1993.jpush.PushResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;

/**
 * Server酱推送
 *
 * @author gcdd1993
 * @date 2020/12/23
 * @since 1.0.0
 */
@Slf4j
public class WxServerPushImpl
        extends AbstractJPushImpl {
    private static final String DEFAULT_URL = "https://{0}/{1}.send";
    /**
     * Server酱推送SCKEY，详情请查看
     * <a href="http://sc.ftqq.com/?c=code"></a>获取Server酱SCKEY
     */
    private final String scKey;

    WxServerPushImpl(String domain, String scKey) {
        super(domain);
        this.scKey = scKey;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PushResult push(String title, String content) {
        String pushUrl = MessageFormat.format(
                DEFAULT_URL,
                domain,
                scKey,
                title,
                content
        );
        try {
            FormBody formBody = new FormBody.Builder()
                    .add("text", title)
                    .add("desp", content)
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
                if (Objects.equals(res.get("success"), true)) {
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
