/**
 * @author aungmyohtet
 */
package com.aungmyohtet.java.application.chat.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageReceiverWorker implements Runnable {
    
    private ChatClient chatClient;

    private List<MessageReceiveHandler> messageReceiveHandlers = new ArrayList<>();

    public MessageReceiverWorker(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = this.chatClient.receive();
                for (MessageReceiveHandler messageReceiveHandler : messageReceiveHandlers) {
                    messageReceiveHandler.messageReceived(message);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }
        }
    }

    public void stop() throws IOException {
        this.chatClient.disconnect();
    }

    public void addMessageReceiveHandler(MessageReceiveHandler messageReceiveHandler) {
        this.messageReceiveHandlers.add(messageReceiveHandler);
    }

}
