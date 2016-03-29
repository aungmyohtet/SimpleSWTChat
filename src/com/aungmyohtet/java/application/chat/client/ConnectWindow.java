package com.aungmyohtet.java.application.chat.client;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.aungmyohtet.java.application.chat.MessageCode;

public class ConnectWindow {

	protected Shell shell;

	private Text textHostIP;

	private Text textPortNo;

	private Text textUserName;

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
		shell.setSize(360, 250);
		shell.setText("Connect to chat server");
		shell.setLayout(new GridLayout(2, false));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label lblHostIP = new Label(shell, SWT.NONE);
		lblHostIP.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHostIP.setText("Host IP:");

		textHostIP = new Text(shell, SWT.BORDER);
		textHostIP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textHostIP.setText("localhost");

		Label lblPortNo = new Label(shell, SWT.NONE);
		lblPortNo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPortNo.setText("Port No:");

		textPortNo = new Text(shell, SWT.BORDER);
		textPortNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textPortNo.setText("9977");

		Label lblUserName = new Label(shell, SWT.NONE);
		lblUserName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUserName.setText("User Name:");

		textUserName = new Text(shell, SWT.BORDER);
		textUserName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textUserName.forceFocus();
		new Label(shell, SWT.NONE);

		Button btnConnect = new Button(shell, SWT.NONE);
		btnConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String hostAddress = textHostIP.getText();
				int portNo = Integer.parseInt(textPortNo.getText());
				try {
					Socket socket = new Socket(hostAddress, portNo);
					ChatClient chatClient = new ChatClient(socket);
					MessageReceiverWorker clientWorker = new MessageReceiverWorker(chatClient);
					ChatWindow chatWindow = new ChatWindow(chatClient);
					clientWorker.addMessageReceiveHandler(chatWindow);
					Thread clientThread = new Thread(clientWorker);
					clientThread.start();
					chatClient.send(MessageCode.USER_ONLINE + textUserName.getText());
					shell.close();
					chatWindow.open();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		btnConnect.setText("Connect");

		shell.setDefaultButton(btnConnect);

	}

}
