package com.group29.skipbo.net;

public final class ServerMessageParser {

    private ServerMessageParser() {}

    public static ServerMessage parse(String line) {
        String[] parts = line.split("~");
        String cmd = parts[0];
        String[] args = new String[Math.max(0, parts.length - 1)];
        if (args.length > 0) System.arraycopy(parts, 1, args, 0, args.length);
        return new ServerMessage(cmd, args, line);
    }
}