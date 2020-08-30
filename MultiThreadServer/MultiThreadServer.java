import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class MultiThreadServer extends JFrame {
    private JTextArea jta = new JTextArea();

    public static void main(String[] args) {
        // launch server
        new MultiThreadServer();
    }

    public MultiThreadServer() {
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), BorderLayout.CENTER);

        setTitle("Multithread Server");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Create a multithread server
        try {
            // create a server socket
            ServerSocket serverSocket = new ServerSocket(5000);
            jta.append("Multithread Sever started at: " +  new Date() + "\n");

            // client counter
            int clientNb = 1;

            while (true) {
                // listen for a new connection request
                Socket socket = serverSocket.accept();

                // display client number
                InetAddress inetAddress = socket.getInetAddress();
                jta.append("Client nb." + clientNb + "'s host name is: " + inetAddress.getHostName() + "\n");
                jta.append("Client nb." + clientNb + "'s IP address is: " + inetAddress.getHostAddress() + "\n");

                // create a new thread for the connection
                HandleAClient task = new HandleAClient(socket);

                // start the new thread
                new Thread(task).start();

                clientNb++; 
            }
        }
        catch (IOException ex) {
                System.err.println(ex);
        }
    }

    // Inner class
    // Define the thread class for handling new connection
    class HandleAClient implements Runnable {
        private Socket socket; // a connected socket

        /** Construct a thread*/
        public HandleAClient(Socket socket) {
            this.socket = socket;
        }

        /** Run a thread */
        public void run() {
            try {
                // create data i/o stream
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

                // continuously serve the clients
                while (true) {
                    // recieve data
                    double radius = inputFromClient.readDouble();

                    // compute area
                    double area = radius * radius * Math.PI;

                    // send output to the client
                    outputToClient.writeDouble(area);

                    jta.append("Radius recieved from client: " + radius + "\n");
                    jta.append("Area found: " + area + "\n");
                }
            }
            catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }  
}