import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

    private JTextField jtf = new JTextField();
    private JTextArea jta =  new JTextArea();

    // IO sreams
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    public static void main(String[] args) {

        // launch client
        new Client();
    }

    public Client() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("Enter radius"), BorderLayout.WEST);
        panel.add(jtf, BorderLayout.CENTER);
        jtf.setHorizontalAlignment(JTextField.RIGHT);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(jta), BorderLayout.CENTER);

        // register listener
        jtf.addActionListener(new TextFieldListener());

        setTitle("Client");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        try {
            // Request connection
            Socket socket = new Socket("localhost", 8000); // client socket

            // recieve data from server
            fromServer = new DataInputStream(socket.getInputStream());

            // send data to server
            toServer = new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException ex) {
            jta.append(ex.toString() + '\n');
        }
    }

    private class TextFieldListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                double radius = Double.parseDouble(jtf.getText().trim());

                // send data(raadius) to the server
                toServer.writeDouble(radius);
                toServer.flush();

                // get data(area) from the server
                double area = fromServer.readDouble();

                // display output
                jta.append("Radius is " + radius + ".\n");
                jta.append("Area received from the server is " + area + ".\n");
            }
            catch (IOException ex){
                System.err.println(ex);
            }
        }
    }
}
