package com.autohome.mcpstore.models;

import java.io.Serializable;
import java.util.Collection;


public class Protocol<T> implements Serializable {
    private static final long serialVersionUID = -3374377238757831251L;
    private int returncode;
    private String message;
    private T result;

    public Protocol() {
    }

    public Protocol(T object) {
        this(object, 0, "");
    }

    public Protocol(int returncode, String message) {
        this(null, returncode, message);
    }

    public Protocol(T object, int returncode, String message) {
        this.result = object;
        this.returncode = returncode;
        this.message = message;
    }

    public Protocol(Boolean object, String message) {
        this((T) object, object ? 1 : 0, message);
    }

    public Protocol(Collection list, int pageSize, int pageIndex, int rowCount) {
        this(null, 0, "");
        this.result = (T) new ProtocolPager(list, pageSize, pageIndex, rowCount);
    }

    public int getReturncode() {
        return returncode;
    }

    public void setReturncode(int returncode) {
        this.returncode = returncode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Protocol{"
                + "returncode=" + returncode
                + ", message='" + message + '\''
                + ", result=" + result
                + '}';
    }

}