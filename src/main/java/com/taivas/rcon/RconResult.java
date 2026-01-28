package com.taivas.rcon;

public class RconResult {

    public enum Status {
        FAILED,
        OK,

    }

    private final Status status;
    private final String message;

    public RconResult(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "RconResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
