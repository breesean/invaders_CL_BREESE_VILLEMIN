package org.enstabretagne.invaders;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Représente un client TCP : cette classe s'appuie principalement sur un objet
 * {@link Socket}, et s'initialise par un nom de serveur et un numéro de port
 */
public class ClientTCP {

    private int numeroPort; // ## attribute numeroPort
    private String nomServeur; // ## attribute nomServeur
    private BufferedReader socIn; // ## link socIn
    private PrintStream socOut; // ## link socOut
    private Socket socketServeur; // ## link socketServeur

    /**
     * Création d'un nouveau {@link ClientTCP} avec un nom de serveur et un numéro
     * de port
     *
     * @param unNomServeur
     * @param unNumero
     */
    public ClientTCP(String unNomServeur, int unNumero) {
        numeroPort = unNumero;
        nomServeur = unNomServeur;
    }

    /**
     * Exécute la connexion au serveur, et crée la {@link Socket}
     *
     * Si une exception se produit, elle est traité en interne, et la méthode
     * renvoie false
     *
     * @return true si la connexion s'est bien déroulée
     */
    public boolean connexionServeur() {
        try {
            socketServeur = new Socket(nomServeur, numeroPort);
            socOut = new PrintStream(socketServeur.getOutputStream());
            socIn = new BufferedReader(new InputStreamReader(socketServeur.getInputStream()));

        } catch (UnknownHostException e) {
            System.err.println("Serveur inconnu : " + e);
            return false;
        } catch (Exception e) {
            System.err.println("Exception:  " + e);
            return false;
        }

        return true;
    }

    /**
     * Commande la déconnexion au serveur
     */
    public void deconnexionServeur() {
        try {
            socOut.close();
            socIn.close();
            socketServeur.close();
        } catch (Exception e) {
            System.err.println("Exception:  " + e);
        }
    }

    /**
     * Transmet une chaine de caractères sur la Socket, et retourne la réponse sous
     * la forme d'une chaine.
     *
     * Cette méthode nécessite que la connexion soit effective
     *
     * @param uneChaine
     * @return
     */
    public String transmettreChaine(String uneChaine) {
        String msgServeur = null;
        try {

            System.out.println("Client " + uneChaine);

            socOut.println(uneChaine);
            socOut.flush();

            msgServeur = socIn.readLine();
            System.out.println("Client msgServeur " + msgServeur);
            return msgServeur;

        } catch (UnknownHostException e) {
            System.err.println("Serveur inconnu : " + e);
            return null;
        } catch (Exception e) {
            System.err.println("Exception:  " + e);
            return null;
        }
    }

    /**
     * Enchaînement d'une connexion au serveur, de la transmission d'une chaine de
     * caractère, et de la déconnexion
     *
     * @param uneChaine
     * @return
     */
    public String connexionTransmettreChaine(String uneChaine) {
        String msgServeur = null;
        if (connexionServeur() == true) {
            try {

                System.out.println("Client " + uneChaine);
                socOut.println(uneChaine);
                socOut.flush();

                msgServeur = socIn.readLine();
                System.out.println("Client msgServeur " + msgServeur);
                deconnexionServeur();
            } catch (Exception e) {
                System.err.println("Exception:  " + e);
            }
        }

        return msgServeur;
    }

}

