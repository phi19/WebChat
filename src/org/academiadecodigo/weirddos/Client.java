package org.academiadecodigo.weirddos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Client {

    private String hostname;
    private int port = 7777;
    private BufferedReader userInput;
    private Socket clientSocket;
    private String username;
    // later I'll have here the bufferedreader userinput
    private ServerSocket serverSocket;

    public void start() {
        try {
            connect();

            login();

            Thread receiverThread = new Thread(new Receiver());

            receiverThread.start();

            Thread senderThread = new Thread(new Sender());

            senderThread.start();

        } catch (UnknownHostException ex) {

            System.out.println("\nUnknown host: " + hostname + ":" + port);

        } catch(SocketTimeoutException ex) {

            System.out.println("\nCouldn't connect to" + hostname + ":" + port + " SUCCESSFULLY!");

        } catch (NumberFormatException ex) {

            System.out.println("Invalid port!");

        } catch (IOException ex) {

            System.out.println(ex.getMessage());
            ex.printStackTrace();

        }

    }

    public void connect() throws IOException {
        userInput = new BufferedReader(new InputStreamReader(System.in));

        //System.out.println("Insert hostname: ");
        System.out.print("Insert hostname: ");
        hostname = userInput.readLine();

        System.out.print("Insert port: ");
        port = Integer.parseInt(userInput.readLine());

        System.out.println("\nTrying to establish the connection, please wait...");

        clientSocket = new Socket(hostname, port);

        System.out.println("Connected to " + clientSocket.getInetAddress().getHostAddress() + ":" + port);

    }

    public void login() throws IOException {

        System.out.print("\nInsert username: ");
        username = userInput.readLine();

        // later will send a "welcome to the chat, username

        // later asks for password
        // later makes a register
    }

    private class Receiver implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader receiverUserInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (true) {

                    System.out.println(receiverUserInput.readLine());
                }

            } catch (IOException ex) {

                System.out.println(ex.getMessage());
                ex.printStackTrace();

            }
        }
    }

    private class Sender implements Runnable {

        public void run() {
            try {
                // create an enum for the possible commands
                // if they are send, terminate
                PrintWriter userOutput = new PrintWriter(clientSocket.getOutputStream(), true);

                while (true) {
                    String prefix = "\n" + username + ": ";

                    String msg = userInput.readLine();

                    userOutput.println(prefix + msg);
                }

            } catch (IOException ex) {

                System.out.println(ex.getMessage());
                ex.printStackTrace();

            }
        }
    }
}
