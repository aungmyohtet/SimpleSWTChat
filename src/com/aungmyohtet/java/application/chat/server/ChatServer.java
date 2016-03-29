/**
 * 
 * @author aungmyohtet
 */
package com.aungmyohtet.java.application.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.aungmyohtet.java.application.chat.MessageCode;

public class ChatServer {
	private List<ChatClient> clients = new ArrayList<>();
	private List<ChatServiceWorker> serviceWorkers = new ArrayList<>();
	int clientId;
	int portNo;
	boolean stopped;

	public ChatServer(int portNo) {
		this.portNo = portNo;
	}

	public void start() throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(portNo);) {
			while (true && !stopped) {
				Socket clientSocket = serverSocket.accept();
				ChatClient chatClient = new ChatClient(++clientId, clientSocket);
				ChatServiceWorker chatServiceWorker = new ChatServiceWorker(this, chatClient);
				clients.add(chatClient);
				serviceWorkers.add(chatServiceWorker);
				Thread serviceThread = new Thread(chatServiceWorker);
				serviceThread.start();
			}

		} catch (IOException e) {
			throw e;
		}

	}

	public void stop() throws IOException {
		this.stopped = true;
		for (ChatClient client : clients) {
			client.disconnect();
		}
		for (ChatServiceWorker serviceWorker : serviceWorkers) {
			serviceWorker.stop();
		}
	}

	public void processClientMessage(ChatClient sendingClient, String rawMessage) {
		System.out.println("Processing received message");
		System.out.println("Messsage is:" + rawMessage);
		String messageCode = rawMessage.substring(0,3);
		String messageBody = rawMessage.substring(3);
		if (messageCode.equals(MessageCode.MESSAGE)) {
			String fromId = String.valueOf(sendingClient.getClientId());
			String[] messageParts = messageBody.split("::");
			String toId = messageParts[0];
			String messageContent = messageParts[1];
			String messageToSend = MessageCode.MESSAGE + fromId + "::" + sendingClient.getClientName() + "::"
					+ messageContent;
			sendMessageToTargetClient(toId, messageToSend);
		} else if (messageCode.equals(MessageCode.USER_ONLINE)) {
			sendUserListMessage(sendingClient);

			String newClientName = messageBody;//This assignment for readability
			sendingClient.setClientName(newClientName);
			String newUserOnlineMessage = MessageCode.USER_ONLINE + sendingClient.getClientId() + "," + newClientName;
			brodcastNewUserOnlineMessage(sendingClient.getClientId(), newUserOnlineMessage);
		}
	}

	public void sendUserListMessage(ChatClient toClient) {
		StringBuilder userListMessage = new StringBuilder(MessageCode.USER_LIST);
		for (ChatClient client : clients) {
			if (client.getClientId() != toClient.getClientId()) {
				userListMessage.append(client.getClientId() + "," + client.getClientName() + "#");
			}
		}

		toClient.send(userListMessage.toString());
	}

	public void brodcastNewUserOnlineMessage(int newOnlineClientId, String userOnlineMessage) {
		for (ChatClient client : clients) {
			if (client.getClientId() != newOnlineClientId) {
				client.send(userOnlineMessage);
				System.out.println("Sent to client " + client.getClientId() + " message: " + userOnlineMessage);
			}
		}
	}

	public void sendMessageToTargetClient(String toId, String message) {
		for (ChatClient client : clients) {
			if (toId.equals(String.valueOf(client.getClientId()))) {
				client.send(message);
				System.out.println("Sent to client " + toId + " message: " + message);
				return;
			}
		}
	}

}
