package chat_frontend;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends Frame {
	
	Boolean ShowDebug = false;
	
	Label Component_Title_Label = new Label("Welcome! To proceed, please login.");
	Label Component_Nickname_Label = new Label("Nickname: ");
	TextField Component_Nickname = new TextField(10);
	Label Component_PIN_Label = new Label("PIN: ");
	TextField Component_PIN = new TextField(10);
	Button Component_Login = new Button("Login");
	Button Component_Register = new Button("Register");
	Button Component_ForgotPIN = new Button("Forgot PIN");
	
	public String RegisterAgent_Address;
	
	public Login() {
		super("Chat: Login");
		this.setActionListeners();
		this.setSize(320, 290);
		this.GUI();
	}
	
	public void sendRequest(String recipient, String payload) {
		
	}
	
	public void setActionListeners() {
		this.Component_Login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent x){
				String Nickname = Component_Nickname.getText();
				String PIN = Component_PIN.getText();
				
				if(ShowDebug == true) {
					System.out.println("DEBUG: LOGIN");
					System.out.println("DEBUG: Nickname = " + Nickname);
					System.out.println("DEBUG: PIN = " + PIN);					
				}
				
				Map<String, String> Request = new HashMap<String, String>();
				Request.put("OP", "LOGIN");
				Request.put("NK", Nickname);
				Request.put("PW", PIN);
				sendRequest(RegisterAgent_Address, Request.toString());
			}
		});
		this.Component_Register.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent x){
				String Nickname = Component_Nickname.getText();
				String PIN = Component_PIN.getText();
				
				if(ShowDebug == true) {
					System.out.println("DEBUG: REGISTER");
					System.out.println("DEBUG: Nickname = " + Nickname);
					System.out.println("DEBUG: PIN = " + PIN);					
				}
				
				Map<String, String> Request = new HashMap<String, String>();
				Request.put("OP", "REGISTER");
				Request.put("NK", Nickname);
				Request.put("PW", PIN);
				sendRequest(RegisterAgent_Address, Request.toString());
			}
		});
		this.Component_ForgotPIN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent x){
				String Nickname = Component_Nickname.getText();
				
				if(ShowDebug == true) {
					System.out.println("DEBUG: FORGOT PIN");
					System.out.println("DEBUG: Nickname = " + Nickname);					
				}
				
				Map<String, String> Request = new HashMap<String, String>();
				Request.put("OP", "RECOVER");
				Request.put("NK", Nickname);
				sendRequest(RegisterAgent_Address, Request.toString());				
				
			}
		});
	}
	
	public Panel GUI_setFieldsPanel() {
		Panel NicknamePanel = new Panel();
		NicknamePanel.setLayout(new BorderLayout(5,5));
		NicknamePanel.add("West",Component_Nickname_Label);
		NicknamePanel.add("East",Component_Nickname);
		
		Panel PINPanel = new Panel();
		PINPanel.setLayout(new BorderLayout(5,5));
		PINPanel.add("West",Component_PIN_Label);
		PINPanel.add("East",Component_PIN);
		
		Panel FieldsContainer = new Panel();
		FieldsContainer.setLayout(new BorderLayout(5,5));
		FieldsContainer.add("North", NicknamePanel);
		FieldsContainer.add("Center", PINPanel);
		return FieldsContainer;
	}
	
	public Panel GUI_setButtonsPanel() {
		Panel Buttons = new Panel();
		Buttons.setLayout(new BorderLayout(5,5));
		Buttons.add("West",Component_Login);
		Buttons.add("East",Component_Register);
		Buttons.add("South",Component_ForgotPIN);
		return Buttons;
	}	

	public void GUI() {
		setBackground(Color.lightGray);
		GridBagLayout GBL = new GridBagLayout();
		setLayout(GBL);
		Panel P1 = new Panel();
		P1.setLayout(new BorderLayout(5,5));
		P1.add("North",Component_Title_Label);
		
		Panel P2 = this.GUI_setFieldsPanel();
		Panel P3 = this.GUI_setButtonsPanel();
		
		P1.add("Center", P2);
		P1.add("South", P3);
		
		GridBagConstraints P1C=new GridBagConstraints();
		P1C.gridwidth=GridBagConstraints.REMAINDER;
		GBL.setConstraints(P1,P1C);
		add(P1);
	}
	
	public void setDebug(Boolean a) {
		this.ShowDebug = a;
	}
	
	
	
}
