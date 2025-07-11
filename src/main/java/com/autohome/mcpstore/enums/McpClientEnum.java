package com.autohome.mcpstore.enums;

public enum McpClientEnum {
    CLINE("cline"),
    TRAE("trae"),
    COPILOT_FOR_WORKSPACE("copilot(project)"),
    COPILOT_FOR_GLOBAL("copilot(global)"),
    CURSOR_FOR_WORKSPACE("cursor(project)"),
    CURSOR_FOR_GLOBAL("cursor(global)"),
    LINGMA("lingma");
    private final String name;

    McpClientEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
