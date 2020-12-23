package io.github.gcdd1993.jpush.internal;

import io.github.gcdd1993.jpush.JPush;
import io.github.gcdd1993.jpush.PushResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author gcdd1993
 * @date 2020/12/23
 * @since 1.0.0
 */
class JPushFactoryTest {
    private static final Logger log = LoggerFactory.getLogger(JPushFactoryTest.class);

    @Test
    void buildWxServer() {
        JPush jPush = JPushFactory.buildWxServer(System.getenv("SCKEY"));
        PushResult pushResult = jPush.push("jpush test", "# markdown test\n - 111");
        log.info(pushResult.toString());
    }
}