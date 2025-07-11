package com.autohome.mcpstore.enums;

public enum TransportTypeEnum {
    STDIO("stdio"),
    SSE("sse");

    private final String value;

    TransportTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
