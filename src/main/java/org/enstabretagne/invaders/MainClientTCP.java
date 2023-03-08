package org.enstabretagne.invaders;

import java.net.*;
import java.io.*;
import java.awt.event.KeyEvent;
public class MainClientTCP {
    private static Socket socket;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        socket = new Socket("localhost", 7777);
        System.out.println("Connected to server");

        // Create input and output streams
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        // Send keyboard input to the server
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            //Send keyboard input
            String inputString = keyboard.readLine();
            int inputKeyCode = KeyEvent.getExtendedKeyCodeForChar(inputString.charAt(0));
            out.writeObject(inputKeyCode);
            out.flush();



            // Receive response from server
            //String response = (String) in.readObject();
            //System.out.println("Server response: " + response);
        }
    }


}
