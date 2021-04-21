package org.academiadecodigo.weirddos;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int port;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private LinkedList<ServerWorker> serverWorkers;

    public Server() {
        serverWorkers = new LinkedList<>();
    }

    public void start() {
        try {
            inputUser();

            createServer();

            listen();
        } catch (IOException ex) {

            System.out.println("Some IO error occurred in ServerWorker class");

            ex.printStackTrace();

        }
    }

    public void inputUser() throws IOException {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Port to bind to: ");
        port = Integer.parseInt(userInput.readLine());
    }

    public void createServer() throws IOException {
        System.out.print("\nCreating a new server in port " + port);

        serverSocket = new ServerSocket(port);

        System.out.print("\nServer started on port " + port);
    }

    public void listen() throws IOException {

        ExecutorService threadPool = Executors.newFixedThreadPool(20);

        while (true) {

            System.out.println("\nWaiting for a client connection...");

            clientSocket = serverSocket.accept();

            ServerWorker serverWorker = new ServerWorker(clientSocket);

            serverWorkers.add(serverWorker);

            threadPool.submit(serverWorker);

            System.out.println("Client connected successfully");
        }
    }


    private class ServerWorker implements Runnable {

        private Socket clientSocket;

        public ServerWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {

            try {

                BufferedReader bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (true) {

                    String message = bReader.readLine(); // later I have to consider the hypotesis of having many lines

                    sendAll(message);
                }

            } catch (IOException ex) {

                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }

        public void sendAll(String message) throws IOException {
            for (ServerWorker serverWorker : serverWorkers) {
                serverWorker.send(message);
            }
        }

        public void send(String message) throws IOException {

            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

            output.println(message);
        }
    }
}
