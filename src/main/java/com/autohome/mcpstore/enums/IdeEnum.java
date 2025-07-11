package com.autohome.mcpstore.enums;

public enum IdeEnum {
    VSCODE("vscode"),
    TRAE("trae"),
    CURSOR("cursor"),
    UNKNOWN("unknown");

    private final String value;

    IdeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
