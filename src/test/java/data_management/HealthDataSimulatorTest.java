package data_management;

import com.cardiogenerator.HealthDataSimulator;
import com.data_management.DataReaderReal;
import com.data_management.DataStorage;
import com.data_management.WebSocketClientReal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import org.java_websocket.handshake.ServerHandshake;

public class HealthDataSimulatorTest {

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        int port = 8080;

        // Run HealthDataSimulator with the WebSocket output strategy
        Thread simulatorThread = new Thread(() -> {
            try {
                HealthDataSimulator.main(new String[]{"--patient-count", "1", "--output", "websocket:" + port});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        simulatorThread.start();

        // Latch to wait for client connection
        CountDownLatch latch = new CountDownLatch(1);

        // Wait for a short period to ensure the server has started
        Thread.sleep(2000);

        // Set up WebSocketClientReal
        DataReaderReal reader = new DataReaderReal();
        DataStorage dataStorage = DataStorage.getInstance(reader);
        WebSocketClientReal client = new WebSocketClientReal(new URI("ws://localhost:" + port), dataStorage) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                latch.countDown(); // Release the latch when connected
            }

            @Override
            public void onMessage(String message) {
                super.onMessage(message);
                System.out.println("Client received message: " + message);
            }
        };

        client.connect();

        // Wait for the client to connect
        latch.await();

        // Keep the test running to observe interactions
        Thread.sleep(10000);

        // Stop the client
        client.close();
        simulatorThread.interrupt();
    }
}
