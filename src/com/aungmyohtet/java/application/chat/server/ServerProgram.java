package com.aungmyohtet.java.application.chat.server;

import java.io.IOException;

public class ServerProgram {

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(9977);
        try {
			chatServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
