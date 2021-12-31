package registeragent;

import java.util.*;

public class RegisterAgent {
	
	Integer Port;
	String NameserverAddress;
	Socket socket;
	Map<String, String> pendingRequests;
	
	
	public RegisterAgent(Integer Port, String NameserverAddress) {
		this.Port = Port;
		this.NameserverAddress = NameserverAddress;
		this.pendingRequests = new HashMap<String, String>();
	}
	
	public Socket getSocket() {
		return this.socket;
	}	
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}	
	
	public void sendPayload(String recipient, String payload) {
		String[] recipientA = recipient.split(":");
		this.getSocket().sendDatagramPacket(Integer.valueOf(recipientA[1]), payload, recipientA[0]);
	}
	
	public void handleRequest(String sender, String request) {
		printDebug(sender);
		printDebug(request);
		Map<String, String> req = this.formatRequest(request);
		if(req.get("APP") == "FRONTEND") {
			String Id = UUID.randomUUID().toString();
			req.put("Id", Id);
			this.sendPayload(this.NameserverAddress, req.toString());			
		}
		if(req.get("APP") == "NS") {
			String Id = req.get("Id");
			String PendingRequest_Recipient = (Id != null) ? this.pendingRequests.remove(Id) : null;
			if(Id == null) {
				printDebug(req.toString());
			} else {
				// Tratar sucesso ou erro
				this.sendPayload(PendingRequest_Recipient, req.toString());	
			}
		}
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

	public static void printDebug(String message) {
		System.out.println("DEBUG: " + message);
	}	

}