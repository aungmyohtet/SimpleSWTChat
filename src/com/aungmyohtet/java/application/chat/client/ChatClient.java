package com.aungmyohtet.java.application.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
	
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	
	public ChatClient(Socket socket) throws IOException {
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.writer = new PrintWriter(socket.getOutputStream(), true);
	}

	public void send(String message) {
		writer.println(message);
	}
	
	public String receive() throws IOException {
		return reader.readLine();
	}
	
	public void disconnect() throws IOException {
		this.reader.close();
		this.writer.close();
		this.socket.close();
	}
}
