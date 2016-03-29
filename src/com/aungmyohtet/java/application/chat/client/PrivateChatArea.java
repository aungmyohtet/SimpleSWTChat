/**
 * @author aungmyohtet
 */
package com.aungmyohtet.java.application.chat.client;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aungmyohtet.java.application.chat.MessageCode;

public class PrivateChatArea {
	private Composite composite;
	private Composite compositeChat;
	private ScrolledComposite scrolledComposite;
	private int chattingClientId;
	private ChatWindow chatWindow;

	public PrivateChatArea(int chattingClientId, ChatWindow chatWindow) {
		this.chattingClientId = chattingClientId;
		this.chatWindow = chatWindow;
	}

	public void create() {
		composite = new Composite(chatWindow.compositeRight, SWT.FILL);

		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		scrolledComposite = new ScrolledComposite(composite, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		compositeChat = new Composite(scrolledComposite, SWT.NONE);
		compositeChat.setBackground(ColorRegistry.CHAT_COMPOSITE_BACKGROUND);
		
		GridLayout gl_compositeLeft = new GridLayout(1,false);
		gl_compositeLeft.marginLeft = 40;
		gl_compositeLeft.marginRight = 80;
		compositeChat.setLayout(gl_compositeLeft);

		scrolledComposite.setContent(compositeChat);
		scrolledComposite.setMinSize(compositeChat.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		final Text textChat = new Text(composite, SWT.BORDER);
		textChat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		textChat.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.keyCode == SWT.CR) {
					addNewMessage("me", textChat.getText(), true);
					String messageContent = textChat.getText();
					textChat.setText("");
					String message = MessageCode.MESSAGE + PrivateChatArea.this.chattingClientId + "::"
							+ messageContent;
					PrivateChatArea.this.chatWindow.sendMessage(message);
				}
			}
		});
	}

	public Composite getComposite() {
		return this.composite;
	}

	public void addNewMessage(final String userName, final String message, final boolean mine) {
		Label lblUserLabel = new Label(compositeChat, SWT.NONE);
		lblUserLabel.setText(userName);
		StyledText styledTextMessage = new StyledText(compositeChat, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		styledTextMessage.setText(message);
		GridData gd_styledTextMessage = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		styledTextMessage.setLayoutData(gd_styledTextMessage);
		if (mine) {
			styledTextMessage.setBackground(ColorRegistry.OWN_MESSAGE_COLOR);
		} else {
			styledTextMessage.setBackground(ColorRegistry.OTHER_MESSAGE_COLOR);
		}

		styledTextMessage.setEditable(false);
		styledTextMessage.setCaret(null);
		styledTextMessage.setBottomMargin(5);
		styledTextMessage.setTopMargin(5);
		compositeChat.layout();
		scrolledComposite.setContent(compositeChat);
		scrolledComposite.setMinSize(compositeChat.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.layout(true);

	}
}
