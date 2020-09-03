package com.ibotnaru.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    private final Socket socket;
    private final Thread recieveThread; // stream (create streams)
    private final BufferedReader in; // input stream (read data)
    private final BufferedWriter out; //output stream (write data)
    private final TCPConnectionListener eventListener;

    // constructor overloading
    public TCPConnection(TCPConnectionListener eventListener, String ipAddress, int port) throws IOException {
        // call another constructor from(this) constructor
        this(eventListener, new Socket(ipAddress, port));
    }

    // constructor for connection
    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName(("UTF-8"))));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));

        // stream for the listening inputs
        recieveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady((TCPConnection.this));
                    while (!recieveThread.isInterrupted()) {
                        String message = in.readLine();
                        eventListener.onRecieveString(TCPConnection.this, message);
                    }
                } catch (IOException ex) {
                    eventListener.onException(TCPConnection.this, ex);
                } finally {
                    eventListener.onDisconnect((TCPConnection.this));
                }
            }
        });
        recieveThread.start(); // run the stream
    }

    public synchronized void sendMessage(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException ex) {
            eventListener.onException(TCPConnection.this, ex);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        recieveThread.interrupt();
        try {
            socket.close();
        } catch (IOException ex) {
            eventListener.onException(TCPConnection.this, ex);
        }
    }

    @Override
    public  String toString() {
         return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
