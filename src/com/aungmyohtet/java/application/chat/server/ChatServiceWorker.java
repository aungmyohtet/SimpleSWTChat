/**
 * @author aungmyohtet
 */
package com.aungmyohtet.java.application.chat.server;

import java.io.IOException;

public class ChatServiceWorker implements Runnable{

    private ChatClient chatClient;
    private ChatServer chatServer;
    
    private boolean stopped;
    
    public ChatServiceWorker(ChatServer server, ChatClient client) {
        this.chatClient = client;
        this.chatServer = server;
    }
    
    public void run() {
          while (true && !stopped) {
            String message;
            try {
                message = this.chatClient.receive();
                this.chatServer.processClientMessage(this.chatClient, message);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
          }
    }
    
    public void stop() {
    	this.stopped = true;
    }

}
