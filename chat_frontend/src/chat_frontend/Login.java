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
	
	public Socket sock;
	
	public String RegisterAgent_Address;
	
	public Login() {
		super("Chat: Login");
		this.setActionListeners();
		this.setSize(320, 290);
		this.GUI();
		sock = new Socket();
		sock.login = this;
		sock.start();
	}
	
	public void handleRequest(String sender, String request) {
		printDebug("Received DATAGRAM from '" + sender + "', with PAYLOAD '" + request + "'");
		Map<String, String> req = this.formatRequest(request);
		switch(req.get("STATUS")) {
			case "SUCCESS":
				Component_Title_Label.setText(req.get("CODE") + ": " + req.get("MESSAGE"));
				
				//sock.destroy();
				break;
			case "ERROR":
				Component_Title_Label.setText(req.get("CODE") + ": " + req.get("MESSAGE"));
				break;
			default:
				break;
		}
	}

	public void sendRequest(String recipient, String payload) {
		String IP = recipient.split(":")[0];
		String Port = recipient.split(":")[1];
		if(ShowDebug == true) {
			printDebug("PAYLOAD = " + payload);
			printDebug("IP = " + IP);
			printDebug("PORT = " + Integer.valueOf(Port));
		}
		
		sock.sendDatagramPacket(Integer.valueOf(Port), payload, IP);
	}
	
	public void setActionListeners() {
		this.Component_Login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent x){
				String Nickname = Component_Nickname.getText();
				String PIN = Component_PIN.getText();
				if(PIN.matches("[0-9]+") == true) {
					Map<String, String> Request = new HashMap<String, String>();
					Request.put("APP", "FRONTEND");
					Request.put("OP", "LOGIN");
					Request.put("NK", Nickname);
					Request.put("PIN", PIN);
					
					
					sendRequest(RegisterAgent_Address, Request.toString());					
				} else {
					Component_Title_Label.setText("PIN can only contain numbers.");
				}

			}
		});
		this.Component_Register.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent x){
				String Nickname = Component_Nickname.getText();
				String PIN = Component_PIN.getText();
				
				if(PIN.matches("[0-9]+") == true) {
					Map<String, String> Request = new HashMap<String, String>();
					Request.put("APP", "FRONTEND");
					Request.put("OP", "REGISTER");
					Request.put("NK", Nickname);
					Request.put("PIN", PIN);
					
					sendRequest(RegisterAgent_Address, Request.toString());					
				} else {
					Component_Title_Label.setText("PIN can only contain numbers.");
				}
							

			}
		});
		this.Component_ForgotPIN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent x){
				String Nickname = Component_Nickname.getText();
							
				Map<String, String> Request = new HashMap<String, String>();
				Request.put("APP", "FRONTEND");
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
	
	public Map<String, String> formatRequest(String request){
		Map<String, String> response = new HashMap<String, String>();
		String[] splitted = request.replace("{", "").replace("}", "").split(", ");
		for(String item : splitted) {
			String[] keyval = item.split("=");
			response.put(keyval[0], keyval[1]);
		}
		return response;
	}
	
	public void setDebug(Boolean a) {
		this.ShowDebug = a;
	}
	
	public void printDebug(String message) {
		if(this.ShowDebug == true) {
			System.out.println("DEBUG: " + message);	
		}
	}		
	
}
