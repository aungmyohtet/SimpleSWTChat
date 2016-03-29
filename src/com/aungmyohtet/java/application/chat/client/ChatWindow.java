package com.aungmyohtet.java.application.chat.client;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;

import com.aungmyohtet.java.application.chat.MessageCode;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Label;

public class ChatWindow implements MessageReceiveHandler {

	protected Shell shell;

	Composite compositeLeft;

	Composite compositeRight;

	Map<Integer, PrivateChatArea> privateChatAreas = new HashMap<>();

	Map<Integer, Label> userLabels = new HashMap<>();

	StackLayout stackLayout = new StackLayout();

	private ChatClient chatClient;

	private Color oldLabelColor;
	private Label labelSelected;

	public ChatWindow(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(600, 400);
		shell.setText("Chat Application");
		shell.setLayout(new GridLayout(2, false));

		compositeLeft = new Composite(shell, SWT.BORDER);
		compositeLeft.setBackground(ColorRegistry.LEFT_COMPOSITE_BACKGROUND);
		GridLayout gl_compositeLeft = new GridLayout(1, false);
		gl_compositeLeft.marginWidth = 0;
		compositeLeft.setLayout(gl_compositeLeft);
		GridData gd_compositeLeft = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_compositeLeft.widthHint = 200;
		compositeLeft.setLayoutData(gd_compositeLeft);

		compositeRight = new Composite(shell, SWT.NONE);
		compositeRight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeRight.setLayout(stackLayout);

		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				System.exit(0);
			}
		});

	}

	@Override
	public void messageReceived(String message) {
		processMessage(message);
	}

	public void processMessage(String message) {
		System.out.println("Processing received message in client: " + message);
		String messageCode = message.substring(0, 3);
		String messageBody = message.substring(3);
		if (messageCode.equals(MessageCode.MESSAGE)) {
			String[] messageBodyParts = messageBody.split("::");
			final String fromId = messageBodyParts[0];
			final String fromName = messageBodyParts[1];
			final String messageContent = messageBodyParts[2];

			final Label labelToNotify = userLabels.get(Integer.parseInt(fromId));

			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					updateUserLabel(labelToNotify);
					createOrUpdatePrivateChatArea(fromId, fromName, messageContent);
				}
			});

		} else if (messageCode.equals(MessageCode.USER_LIST)) {
			if (!messageBody.isEmpty()) {
				String[] userStrings = messageBody.split("#");
				for (String userString : userStrings) {
					String[] userStringPart = userString.split(",");
					addUserLabel(userStringPart[0], userStringPart[1]);
				}
			}
		} else if (messageCode.equals(MessageCode.USER_ONLINE)) {
			String[] userParts = messageBody.split(",");
			addUserLabel(userParts[0], userParts[1]);
		}
	}

	public void addUserLabel(final String userId, final String userName) {
		//This method will always be called in non-UI thread
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (userId != null && userName != null && !userId.isEmpty() && !userName.isEmpty()) {
					final Label lblNewUser = new Label(compositeLeft, SWT.NONE);
					ChatWindow.this.userLabels.put(Integer.parseInt(userId), lblNewUser);
					lblNewUser.setBackground(ColorRegistry.DEFAULT_USER_COLOR);
					GridData gd_lblNewUser = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
					gd_lblNewUser.heightHint = 30;
					lblNewUser.setLayoutData(gd_lblNewUser);
					lblNewUser.setText("    " + userName);
					lblNewUser.setData(userId);
					lblNewUser.addMouseListener(new MouseAdapter() {

						@Override
						public void mouseDown(MouseEvent arg0) {
							if (ChatWindow.this.labelSelected != null) {
								ChatWindow.this.labelSelected.setBackground(ColorRegistry.DEFAULT_USER_COLOR);
							}
							ChatWindow.this.labelSelected = lblNewUser;
							lblNewUser.setBackground(ColorRegistry.SELECTED_USER_COLOR);
							ChatWindow.this.oldLabelColor = lblNewUser.getBackground();
							PrivateChatArea privateChatArea = privateChatAreas
									.get(Integer.parseInt(lblNewUser.getData().toString()));
							if (privateChatArea == null) {
								privateChatArea = new PrivateChatArea(Integer.parseInt(lblNewUser.getData().toString()),
										ChatWindow.this);
								privateChatArea.create();
								privateChatAreas.put(Integer.parseInt(lblNewUser.getData().toString()),
										privateChatArea);
							}
							stackLayout.topControl = privateChatArea.getComposite();
							compositeRight.layout();
						}
					});

					lblNewUser.addMouseTrackListener(new MouseTrackAdapter() {

						@Override
						public void mouseEnter(MouseEvent arg0) {
							ChatWindow.this.oldLabelColor = lblNewUser.getBackground();
							lblNewUser.setBackground(ColorRegistry.HOVER_USER_COLOR);
						}

						@Override
						public void mouseExit(MouseEvent arg0) {
							lblNewUser.setBackground(ChatWindow.this.oldLabelColor);
						}
					});
					compositeLeft.layout(true);
				}
			}
		});
	}

	public void sendMessage(String message) {
		this.chatClient.send(message);
	}

	private PrivateChatArea createPrivateChatArea(int chattingUserId) {
		PrivateChatArea privateChatArea = new PrivateChatArea(chattingUserId, ChatWindow.this);
		privateChatArea.create();
		privateChatAreas.put(chattingUserId, privateChatArea);
		return privateChatArea;
	}

	private void updateUserLabel(final Label label) {
		if (label != null && label != labelSelected) {
			label.setBackground(ColorRegistry.MSG_PENDING_USER_COLOR);
		}

	}

	private void createOrUpdatePrivateChatArea(String fromId, String fromName, String messageContent) {
		PrivateChatArea privateChatArea = privateChatAreas.get(Integer.parseInt(fromId));
		if (privateChatArea == null) {
			PrivateChatArea newChatArea = createPrivateChatArea(Integer.parseInt(fromId));
			newChatArea.addNewMessage(fromName, messageContent, false);

		} else {
			privateChatArea.addNewMessage(fromName, messageContent, false);
		}
	}

	public void close() {
		this.shell.close();
	}
}
