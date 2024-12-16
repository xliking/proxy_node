package com.easy.resutils;

/**
 * @author Administrator
 */

public enum REnum {
    SUCCESS(0, "操作成功"),
    RUNTIME_EXCEPTION(1, "系统异常");

    private final int code;
    private final String introduction;

    REnum(int code, String introduction) {
        this.code = code;
        this.introduction = introduction;
    }

    public int getCode() {
        return code;
    }

    public String getIntroduction() {
        return introduction;
    }
}
