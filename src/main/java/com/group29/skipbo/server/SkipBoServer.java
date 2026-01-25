package com.group29.skipbo.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// we use this as the main server entry point
// it listens for connections and spawns a thread for each client
public class SkipBoServer {

    private final int port;
    private final GameController gameController;
    private final List<ClientHandler> clients;
    private boolean running;

    public SkipBoServer(int port) {
        this.port = port;
        this.gameController = new GameController();
        this.clients = new ArrayList<>();
        this.running = false;
    }

    // we start the server and listen for connections
    public void start() {
        running = true;
        ServerView.log("Starting server on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ServerView.log("Server started! Waiting for clients...");

            while (running) {
                try {
                    // we wait for a client to connect
                    Socket clientSocket = serverSocket.accept();
                    ServerView.log("New connection from " + clientSocket.getInetAddress());

                    // we create a handler for this client
                    ClientHandler handler = new ClientHandler(clientSocket, gameController);
                    clients.add(handler);

                    // we start the handler thread
                    Thread thread = new Thread(handler);
                    thread.start();

                } catch (IOException e) {
                    if (running) {
                        ServerView.logError("Error accepting connection: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            ServerView.logError("Could not start server: " + e.getMessage());
        }
    }

    // we stop the server
    public void stop() {
        running = false;
        ServerView.log("Server stopping...");

        // we close all client connections
        for (ClientHandler client : clients) {
            client.disconnect();
        }
        clients.clear();
    }

    // main method to run the server
    public static void main(String[] args) {
        int port = 7777; // default port

        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port, using default 7777");
            }
        }

        System.out.println("=================================");
        System.out.println("    Skip-Bo Game Server");
        System.out.println("=================================");

        SkipBoServer server = new SkipBoServer(port);
        server.start();
    }
}
