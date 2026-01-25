package com.group29.skipbo.net;

// we use this to hold a parsed server message (Java 11 compatible)
public class ServerMessage {

    private final String command;
    private final String[] args;
    private final String raw;

    public ServerMessage(String command, String[] args, String raw) {
        this.command = command;
        this.args = args;
        this.raw = raw;
    }

    public String command() {
        return command;
    }

    public String[] args() {
        return args;
    }

    public String raw() {
        return raw;
    }
}
