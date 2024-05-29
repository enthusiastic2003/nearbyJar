package org.example;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Protobuf generated class for ConnectionRequest
import com.google.location.nearby.connections.proto.OfflineWireFormatsProto.ConnectionRequestFrame;

public class AdvertListner extends Thread {
    private final int PORT;

    public AdvertListner(int PORT) {
        this.PORT = PORT;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT);

            // Loop to handle multiple client connections
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected: " + clientSocket);

                    // Read length prefix
                    DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
                    byte[] lengthPrefix = new byte[4];
                    inputStream.readFully(lengthPrefix);
                    int length = java.nio.ByteBuffer.wrap(lengthPrefix).getInt();

                    System.out.println("Received message of length: " + length);

                    // Read message data
                    byte[] data = new byte[length];
                    int readCheckSum = inputStream.read(data, 0, length);

                    if (readCheckSum != length) {
                        System.out.println("Error in reading data");
                        continue; // Skip this iteration and wait for another connection
                    }

                    // Deserialize ConnectionRequest protobuf message
                    ConnectionRequestFrame connectionRequest = ConnectionRequestFrame.parseFrom(data);

                    // Handle the connection request
                    handleConnectionRequest(connectionRequest);
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }

    private void handleConnectionRequest(ConnectionRequestFrame connectionRequest) {
        // Handle the connection request here
        System.out.println("Received connection request from client: " + connectionRequest.getEndpointInfo());
    }

    public static void main(String[] args) {
        int port = 12345; // Example port
        AdvertListner listener = new AdvertListner(port);
        listener.start();
    }
}
