package com.autohome.mcpstore.enums;

public enum CodeEnum {
    SUCCESS(0),
    ERROR(1);

    private final int value;

    CodeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
