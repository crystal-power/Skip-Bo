package com.group29.skipbo.net;

import protocol.Command;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class SkipBoNetworkClient implements Closeable {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public SkipBoNetworkClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    public void send(Command cmd) {
        String line = cmd.transformToProtocolString();
        out.println(line);
    }

    /**
     * Blocking loop: call from a separate thread.
     */
    public void readLoop(Consumer<String> onLine) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            if (!line.isBlank()) {
                onLine.accept(line);
            }
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}