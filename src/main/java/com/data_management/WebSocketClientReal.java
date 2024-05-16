package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class WebSocketClientReal extends WebSocketClient {

    private DataStorage dataStorage;
    private static final int RECONNECT_INTERVAL = 5000; // 5 seconds
    private Timer reconnectTimer;

    public WebSocketClientReal(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
        this.reconnectTimer = new Timer(true); // Daemon timer
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
        // Cancel any pending reconnections
        reconnectTimer.cancel();
        reconnectTimer = new Timer(true);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        if (isValidMessage(message)) {
            dataStorage.dataReader.readLine(message, dataStorage);
        } else {
            System.err.println("Received corrupted message: " + message);
            // Handle corrupted message scenario
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
        scheduleReconnect();
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("An error occurred: " + ex.getMessage());
        ex.printStackTrace();
    }

    private boolean isValidMessage(String message) {
        // Implement your own logic to check if the message is valid
        // For example, check if the message is not null and matches a specific format
        return message != null && !message.trim().isEmpty();
    }

    private void scheduleReconnect() {
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    reconnect();
                } catch (Exception e) {
                    System.err.println("Reconnection attempt failed: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, RECONNECT_INTERVAL);
    }

    public static void main(String[] args) {
        try {
            DataReader reader = new DataReaderReal();
            DataStorage dataStorage = new DataStorage(reader);
            URI serverUri = new URI("ws://your-websocket-server-uri");
            WebSocketClientReal client = new WebSocketClientReal(serverUri, dataStorage);
            client.connect();

            // To keep the client running
            Thread.currentThread().join();
            
            // The following line is optional, depending on how you want to handle shutdown.
            // client.close();

        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
