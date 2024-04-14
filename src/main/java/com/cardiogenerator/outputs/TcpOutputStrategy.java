package com.cardiogenerator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Outputs given data to a client of a TCP (Transmission Control Protocol) server
 * which are useful since they listen if any client wants to join i, and then provides data to them once they do
 * 
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    public TcpOutputStrategy(int port) {
        /**
         * The constuctor method which creates the TCP server and then looks for any clients that try to join
         * @param port An integer which corresponds ot the port number on whihch You want to create the server
         */
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        /**
         * Overrides the output method from the interface
         * If the "out" variable isn't empty, aka there is an accepted client on the server
         * Then it sends our data to him
         * @param patientId Id of patient to print
         * @param timestamp Time of said observation in a long format
         * @param label Label of said observation
         * @param data Data of said observation to print
         */
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
