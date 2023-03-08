package org.enstabretagne.invaders;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Représente un serveur TCP, qui écoute sur un numéro de port
 *
 */
public class ServeurTCP {

    private int numeroPort;

    ServeurTCP(int unNumeroPort) {
        numeroPort = unNumeroPort;
    }

    public void go() {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(numeroPort);
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + numeroPort + ", " + e);
            System.exit(1);
        }

        // Attention: on décide arbitrairement de ne servir que 4 clients, puis de
        // fermer la connexion
        for (int nbClient = 0; nbClient <= 3; nbClient++) {
            try {
                System.out.println(" Attente du serveur pour la communication d'un client ");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("Accept failed: " + 6666 + ", " + e);
                System.exit(1);
            }
            ServeurSpecifique st = new ServeurSpecifique(clientSocket, this);
            st.start();
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Could not close");
        }
    }



}
