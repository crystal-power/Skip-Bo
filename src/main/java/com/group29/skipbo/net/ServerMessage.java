package com.group29.skipbo.net;

public record ServerMessage(String command, String[] args, String raw) {}
