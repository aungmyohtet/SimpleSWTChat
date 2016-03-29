/**
 * @author aungmyohtet
 */
package com.aungmyohtet.java.application.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    private int clientId;

    private String clientName;

    private Socket clientSocket;

    private BufferedReader networkBufferedReader;

    private PrintWriter networkPrintWriter;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String userName) {
        this.clientName = userName;
    }

    public int getClientId() {
        return this.clientId;
    }

    public ChatClient(int id, Socket socket) throws IOException {
        this.clientId = id;
        this.clientSocket = socket;
        networkBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        networkPrintWriter = new PrintWriter(socket.getOutputStream(), true);

    }

    public void disconnect() throws IOException {
        this.clientSocket.close();
        this.networkBufferedReader.close();
        this.networkPrintWriter.close();

    }

    public void send(String message) {
        this.networkPrintWriter.println(message);
    }

    public String receive() throws IOException {
        return networkBufferedReader.readLine();
    }
}
