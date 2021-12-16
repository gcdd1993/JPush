package io.github.gcdd1993.jpush.internal;

import io.github.gcdd1993.jpush.JPushType;
import io.github.gcdd1993.jpush.PushResult;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gcdd1993
 * @since 2021/12/16
 */
class JPushExecutorTest {

    @Test
    public void push() {
        JPushExecutor jPushExecutor = new JPushExecutor();
        Map<String, String> config = new HashMap<>();
        config.put("scKey", "SCT104389TN7Hw4RD45Rvv5oXWJF46G2ri");
        jPushExecutor.add(JPushType.SCT, config);
        List<PushResult> pushResults = jPushExecutor.push("测试", "测试");
        System.out.println(pushResults);
    }

}