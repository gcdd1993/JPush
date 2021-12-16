package io.github.gcdd1993.jpush;

import lombok.Builder;
import lombok.Data;

/**
 * @author gcdd1993
 * @date 2020/12/23
 * @since 1.0.0
 */
@Data
@Builder
public class PushResult {
    private int code;
    private String msg;
    private boolean success;

    public static PushResult success(String msg) {
        return PushResult.builder()
                .code(200)
                .msg(msg)
                .success(true)
                .build();
    }

    public static PushResult fail(String msg) {
        return fail(502, msg);
    }

    public static PushResult fail(int code, String msg) {
        return PushResult.builder()
                .code(code)
                .msg(msg)
                .success(false)
                .build();
    }
}
