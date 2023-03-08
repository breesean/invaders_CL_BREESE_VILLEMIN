package org.enstabretagne.invaders;

import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;

public class MainServeurTCP {

    private static ServerSocket serverSocket;
    private static Socket player1Socket;
    private static Socket player2Socket;
    private static ObjectInputStream player1In;
    private static ObjectOutputStream player1Out;
    private static ObjectInputStream player2In;
    private static ObjectOutputStream player2Out;

    private static int port;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        port = 7777;

        serverSocket = new ServerSocket(port);
        System.out.println("Server is running on port " + port);

        // Wait for player 1 to connect
        player1Socket = serverSocket.accept();
        System.out.println("Player 1 connected");
        player1Out = new ObjectOutputStream(player1Socket.getOutputStream());
        player1Out.flush();
        player1In = new ObjectInputStream(player1Socket.getInputStream());

        // Wait for player 2 to connect
        player2Socket = serverSocket.accept();
        System.out.println("Player 2 connected");
        player2Out = new ObjectOutputStream(player2Socket.getOutputStream());
        player2Out.flush();
        player2In = new ObjectInputStream(player2Socket.getInputStream());

        // Start the game loop
        while (true) {
            // Receive data from player 1
            Object player1Data = player1In.readObject();
            // Send data to player 2
            player2Out.writeObject(player1Data);
            player2Out.flush();

            // Receive data from player 2
            Object player2Data = player2In.readObject();
            // Send data to player 1
            player1Out.writeObject(player2Data);
            player1Out.flush();

            // Process player input
            if (player1Data instanceof String) {
                // Player 1 sent keyboard input
                String inputString = KeyEvent.getKeyText((Integer) player1Data);
                System.out.println("Client input1 : " + inputString);
                // Process the input and update the game state
                // ...
            }
            if (player2Data instanceof String) {
                // Player 2 sent keyboard input
                String inputString = KeyEvent.getKeyText((Integer) player2Data);
                System.out.println("Client input2 : " + inputString);

                // Process the input and update the game state
                // ...
            }
        }



    }
}


