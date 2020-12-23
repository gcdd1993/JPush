package io.github.gcdd1993.jpush.internal;

import io.github.gcdd1993.jpush.PushResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * @author gcdd1993
 * @date 2020/12/23
 * @since 1.0.0
 */
class JPushFactoryTest {
    private static final Logger log = LoggerFactory.getLogger(JPushFactoryTest.class);
    private final JPushFactory jPushFactory = new JPushFactory();

    @Test
    void buildWxServer() {
        jPushFactory.addWxServer(System.getenv("SCKEY"));
        Map<String, PushResult> pushResult = jPushFactory.push("jpush test", "# markdown test\n - 111");
        log.info(pushResult.toString());
    }

    @Test
    void buildPushPlus() {
        jPushFactory.addPushPlus(System.getenv("PUSH_PLUS_TOKEN"), "test_qun1");
        Map<String, PushResult> pushResult = jPushFactory.push("jpush test", "# markdown test\n - 111");
        log.info(pushResult.toString());
    }

    @Test
    void buildCoolPush() {
        jPushFactory.addCoolPush(System.getenv("PUSH_PLUS_TOKEN"), "test_qun1");
        Map<String, PushResult> pushResult = jPushFactory.push("jpush test", "# markdown test\n - 111");
        log.info(pushResult.toString());
    }
}