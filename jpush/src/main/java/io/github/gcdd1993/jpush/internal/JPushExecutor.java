package io.github.gcdd1993.jpush.internal;

import io.github.gcdd1993.jpush.JPush;
import io.github.gcdd1993.jpush.JPushType;
import io.github.gcdd1993.jpush.PushResult;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * JPush执行器，允许添加多个JPush实现，然后一起推送
 *
 * @author gcdd1993
 * @since 2020/12/23
 */
@Slf4j
public class JPushExecutor {

    private final Map<JPushType, JPush> jPushChain = new HashMap<>();

    private final ExecutorService executor;

    public JPushExecutor() {
        // 考虑到推送消息可能不是频繁的，而且不需要非常及时，所以默认使用单线程
        // 注意：此线程池使用无界队列，同时只允许一个任务执行，其他的都排队
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * 指定线程池构建
     *
     * @param executor 线程池
     */
    public JPushExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void add(JPushType type, Map<String, String> config) {
        switch (type) {
            case COOL:
                if (!jPushChain.containsKey(type)) {
                    jPushChain.put(type, new CoolPushJPushImpl(config));
                }
                break;
            case IGOT:
                if (!jPushChain.containsKey(type)) {
                    jPushChain.put(type, new IGotJPushImpl(config));
                }
                break;
            case SCT:
                if (!jPushChain.containsKey(type)) {
                    jPushChain.put(type, new WxServerPushImpl(config));
                }
                break;
            case PUSH_PLUS:
                if (!jPushChain.containsKey(type)) {
                    jPushChain.put(type, new PushPlusJPushImpl(config));
                }
                break;
            default:
                throw new IllegalArgumentException("找不到对应的推送类型");
        }
    }

    /**
     * 异步推送
     *
     * @param title   标题
     * @param content 内容
     * @return 推送结果
     */
    public List<Future<PushResult>> pushAsync(String title, String content) {
        return jPushChain
                .values()
                .stream()
                .map(pusher -> executor.submit(() -> pusher.push(title, content)))
                .collect(Collectors.toList());
    }

    /**
     * 同步推送
     *
     * @param title   标题
     * @param content 内容
     * @return 推送结果
     */
    public List<PushResult> push(String title, String content) {
        return jPushChain
                .values()
                .stream()
                .map(pusher -> pusher.push(title, content))
                .collect(Collectors.toList());
    }

}
