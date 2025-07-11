package com.autohome.mcpstore.enums;

public enum EventEnum {
    INSTALL("install"),
    UNINSTALL("uninstall");

    private final String value;

    EventEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
