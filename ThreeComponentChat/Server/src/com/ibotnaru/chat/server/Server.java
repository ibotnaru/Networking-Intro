package com.ibotnaru.chat.server;

import com.ibotnaru.chat.network.TCPConnection;
import com.ibotnaru.chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


public class Server implements TCPConnectionListener {
    public static void main(String[] args) {
        new Server();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private Server() {
        System.out.println("Server started...");
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException ex) {
                    System.out.println("TCPConnection exception: " + ex);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToClients("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onRecieveString(TCPConnection tcpConnection, String value) {
        sendToClients(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToClients("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception ex) {
        System.out.println("TCPConnection exception: " + ex);
    }

    private void sendToClients(String value) {
        System.out.println(value);
        for(int i = 0; i < connections.size(); i++) {
            connections.get(i).sendMessage(value);
        }
    }
}
