package com.autohome.mcpstore.enums;

public enum OsEnum {
    WINDOWS("windows"),
    MAC("mac"),
    LINUX("linux"),
    UNKNOWN("unknown");

    private final String value;

    OsEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
