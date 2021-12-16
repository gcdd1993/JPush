package io.github.gcdd1993.jpush.internal;

import io.github.gcdd1993.jpush.JPush;
import lombok.extern.slf4j.Slf4j;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.net.Proxy;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author gcdd1993
 * @since 2020/12/23
 */
@Slf4j
abstract class AbstractJPushImpl implements JPush {
    protected final OkHttpClient.Builder okHttpClientBuilder;
    protected final Request.Builder requestBuilder;
    protected final String domain;

    protected AbstractJPushImpl(String domain) {
        this(domain, null);
    }

    protected AbstractJPushImpl(String domain, Proxy proxy) {
        this.okHttpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(Duration.of(3, ChronoUnit.SECONDS))
                .callTimeout(Duration.of(5, ChronoUnit.SECONDS))
                .cookieJar(CookieJar.NO_COOKIES);
        if (proxy != null) {
            this.okHttpClientBuilder
                    .proxy(proxy);
        }
        this.requestBuilder = new Request.Builder();
        this.domain = domain;
    }

}
