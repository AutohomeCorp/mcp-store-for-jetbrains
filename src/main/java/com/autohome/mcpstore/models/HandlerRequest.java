package com.autohome.mcpstore.models;

public class HandlerRequest<T> {
    public String command;
    public T value;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
