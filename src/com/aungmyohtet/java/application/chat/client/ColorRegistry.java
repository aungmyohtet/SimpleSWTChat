package com.aungmyohtet.java.application.chat.client;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorRegistry {

	public static final Color DEFAULT_USER_COLOR = new Color(Display.getCurrent(), new RGB(211,211,211));
	public static final Color HOVER_USER_COLOR = new Color(Display.getCurrent(), new RGB(64,224,208));
	public static final Color SELECTED_USER_COLOR = new Color(Display.getCurrent(), new RGB(72,209,204));
	public static final Color MSG_PENDING_USER_COLOR = new Color(Display.getCurrent(), new RGB(250,128,144));
	
	public static final Color OWN_MESSAGE_COLOR = new Color(Display.getCurrent(), new RGB(173, 216, 230));
    public static final Color OTHER_MESSAGE_COLOR = new Color(Display.getCurrent(), new RGB(135, 206, 235));
    
    public static final Color CHAT_COMPOSITE_BACKGROUND = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
    public static final Color LEFT_COMPOSITE_BACKGROUND = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    
    
    public void dispose() {
    	
    }
}
