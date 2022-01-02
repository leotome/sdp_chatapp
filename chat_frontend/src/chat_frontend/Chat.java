package chat_frontend;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Chat extends Frame {
	TextField Component_Recipient = new TextField(30);
	TextField Component_Message = new TextField(30);
	TextArea Component_Display = new TextArea(10,30);
	Button Component_Send = new Button("Send");
	
	Map<String, Message> PendingRequests;
	
	String RegisterAgent_Address;
	Socket sock;
	Boolean ShowDebug;
	
	String Nickname;
	Integer PIN;
	
	public Chat(String Nickname, Integer PIN, String RegisterAgent_Address) {
		super("Chat: " + Nickname);
		sock = new Socket(PIN);
		sock.chat = this;
		sock.start();
		this.RegisterAgent_Address = RegisterAgent_Address;
		PendingRequests = new HashMap<String, Message>();
		this.setActionListeners();
		this.Nickname = Nickname;
		this.PIN = PIN;
		this.setSize(350, 300);
		this.GUI();
		this.setVisible(true);
	}
	
	public void setActionListeners() {
		this.Component_Send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent x){
				// IN ORDER TO SEND A MESSAGE, WE NEED TO QUERY THE NICKNAMES FIRST
				// STORE THE MESSAGE LOCALLY, QUERY, THEN SEND IF POSSIBLE
				String Id = UUID.randomUUID().toString();
				String Message = Component_Message.getText();
				String Recipient = Component_Recipient.getText();
				printDebug("EVENT SEND ID = " + Id);
				printDebug("EVENT SEND Message = " + Message);
				printDebug("EVENT SEND Recipient = " + Recipient);
				
				Message message = new Message();
				message.Id = Id;
				message.Message = Message;
				message.Recipient = Recipient;
				// STORE THE MESSAGE LOCALLY...
				PendingRequests.put(Id, message);
				
				// ...THEN QUERY THE AGENT
				Map<String, String> request = new HashMap<String, String>();
				request.put("Id", Id);
				request.put("APP", "FRONTEND");
				request.put("OP", "RESOLVE");
				request.put("NK", Recipient);
				
				sendRequest(RegisterAgent_Address, request.toString());
			}
		});
	}	
	
	public void handleRequest(String sender, String request) {
		printDebug("Received DATAGRAM from '" + sender + "', with PAYLOAD '" + request + "'");
		Map<String, String> req = this.formatRequest(request);
		switch(req.get("TYPE")) {
			case "RESOLVE":
				Integer RESOLVE_Success = Integer.valueOf(req.get("SUCCESS"));
				if(RESOLVE_Success > 0) {
					String RESOLVE_Id = req.get("Id");
					Message RESOLVE_PendingMessage = PendingRequests.get(RESOLVE_Id);
					Map<String, String> RESOLVE_SendPayload = new HashMap<String, String>();
					RESOLVE_SendPayload.put("TYPE", "MESSAGE");
					RESOLVE_SendPayload.put("NK", this.Nickname);
					RESOLVE_SendPayload.put("MESSAGE", RESOLVE_PendingMessage.Message);
					for(String key : req.keySet()) {
						if(key.contains("ITEM_ADDRESS") == true) {
							String ItemAddress = req.get(key);
							this.sendRequest(ItemAddress, RESOLVE_SendPayload.toString());
						}
					}
				}
				break;
			case "MESSAGE":
					String MESSAGE_NicknameSender = req.get("NK");
					String MESSAGE_Message = req.get("MESSAGE");
					Component_Display.append("\n" + MESSAGE_NicknameSender + ": " + MESSAGE_Message);
				break;
			default:
				break;
		}
	}

	public void sendRequest(String recipient, String payload) {
		String IP = recipient.split(":")[0];
		String Port = recipient.split(":")[1];
		if(this.ShowDebug == true) {
			printDebug("PAYLOAD = " + payload);
			printDebug("IP = " + IP);
			printDebug("PORT = " + Integer.valueOf(Port));
		}
		sock.sendDatagramPacket(Integer.valueOf(Port), payload, IP);
	}	
	
	public void GUI(){
		setBackground(Color.lightGray);
		Component_Display.setEditable(false);
		GridBagLayout GBL = new GridBagLayout();
		setLayout(GBL);
		Panel P1 = new Panel();
		P1.setLayout(new BorderLayout(5,5));
		P1.add("North",Component_Recipient);
		P1.add("West",Component_Message);
		P1.add("East",Component_Send);
		P1.add("South",Component_Display);
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
