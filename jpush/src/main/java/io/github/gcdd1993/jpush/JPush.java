package io.github.gcdd1993.jpush;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * 聚合推送服务
 *
 * @author gcdd1993
 * @date 2020/12/23
 * @since 1.0.0
 */
public interface JPush {

    /**
     * 同步推送
     *
     * @param title   标题
     * @param content 内容
     * @return 推送结果
     */
    PushResult push(String title, String content);

    /**
     * 异步推送
     *
     * @param title   标题
     * @param content 内容
     * @return 推送结果
     */
    CompletableFuture<PushResult> pushAsync(String title, String content);

}
