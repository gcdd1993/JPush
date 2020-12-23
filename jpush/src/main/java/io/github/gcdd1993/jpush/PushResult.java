package io.github.gcdd1993.jpush;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author gcdd1993
 * @date 2020/12/23
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
public class PushResult {
    private int code;
    private String msg;
    private boolean success;

    public static PushResult success(String msg) {
        PushResult pushResult = new PushResult();
        pushResult.setCode(200);
        pushResult.setMsg(msg);
        pushResult.setSuccess(true);
        return pushResult;
    }

    public static PushResult fail(String msg) {
        return fail(502, msg);
    }

    public static PushResult fail(int code, String msg) {
        PushResult pushResult = new PushResult();
        pushResult.setCode(code);
        pushResult.setMsg(msg);
        pushResult.setSuccess(false);
        return pushResult;
    }
}
