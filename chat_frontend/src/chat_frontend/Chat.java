package chat_frontend;

import java.io.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Chat extends Frame {
	TextField Component_Recipient = new TextField(30);
	TextField Component_Message = new TextField(30);
	TextArea Component_Display = new TextArea(10,30);
	Button Component_Send = new Button("Send");
	
	public Chat() {
		super("Chat");
		this.setSize(320, 290);
		this.GUI();
		this.setVisible(true);
	}
	
	public void GUI(){
		setBackground(Color.lightGray);
		Component_Display.setEditable(false);
		GridBagLayout GBL = new GridBagLayout();
		setLayout(GBL);
		Panel P1 = new Panel();
		P1.setLayout(new BorderLayout(5,5));
		P1.add("North",Component_Message);
		P1.add("West",Component_Recipient);
		P1.add("East",Component_Send);
		P1.add("South",Component_Display);
		GridBagConstraints P1C=new GridBagConstraints();
		P1C.gridwidth=GridBagConstraints.REMAINDER;
		GBL.setConstraints(P1,P1C);
		add(P1);
	}	
	

}
