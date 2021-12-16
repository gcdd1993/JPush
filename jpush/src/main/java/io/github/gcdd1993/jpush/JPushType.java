package io.github.gcdd1993.jpush;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 推送服务类型
 *
 * @author gcdd1993
 * @since 2021/12/16
 */
@RequiredArgsConstructor
public enum JPushType {
    COOL("QQ酷推"),
    IGOT("IGot聚合推送"),
    PUSH_PLUS("推送加"),
    SCT("Server酱");

    @Getter
    private final String desc;

}
