package nameserver;

import java.util.*;

public class NameServer {

	private Socket socket;
	private Map<String, Integer> Users;
	private Integer NameServer_Port;
	
	public Boolean ShowDebug = false;

	public NameServer(Integer NameServer_Port) {
		this.NameServer_Port = NameServer_Port;
		this.Users = new HashMap<String, Integer>();
	}
	
	public Socket getSocket() {
		return this.socket;
	}	
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public boolean loginUser(String Nickname, Integer PIN) {
		if(this.Users.containsKey(Nickname)) {
			Integer PIN_User = this.Users.get(Nickname);
			return PIN_User == PIN;
		}
		return false;
	}
	
	public String registerUser(String Nickname, Integer PIN) {
		if(this.Users.containsKey(Nickname) == true) {
			return "ERROR: This user already exists";
		}
		if(this.Users.containsValue(PIN) == true) {
			return "ERROR: This PIN is being used";
		}
		this.Users.put(Nickname, PIN);
		printDebug("User '" + Nickname + "' was registered sucessfully with PIN '" + PIN + "'");
		return "SUCCESS: The user was created successfully.";
	}
	
	public Integer recoverPIN(String Nickname) {
		printDebug("Requested PIN for User '" + Nickname + "'");
		if(this.Users.containsKey(Nickname)) {
			printDebug("Found PIN: '" + Nickname + "'");
			return this.Users.get(Nickname);
		}
		return -1;
	}

	public void printUsers() {
		if(this.Users.size() > 0) {
			for(String Nickname : this.Users.keySet()) {
				System.out.println("Nickname: " + Nickname);
				System.out.println("PIN: " + this.Users.get(Nickname));
			}			
		} else {
			System.out.println("There are no users registered.");
		}
		
	}
	
	public void sendRequest(String address, String payload) {
		String[] ToAddress = address.split(":");
		printDebug("Destination: '" + address + "'");
		printDebug("Payload to send: '" + payload + "'");
		this.getSocket().sendDatagramPacket(Integer.valueOf(ToAddress[1]), payload, ToAddress[0]);
	}
	
	public void handleRequest(String sender, String request) {
		printDebug("Received DATAGRAM from '" + sender + "', with PAYLOAD '" + request + "'");
		Map<String, String> req = this.formatRequest(request);
		Map<String, String> res = new HashMap<String, String>();
		res.put("APP", "NS");
		System.out.println(req);
		if(req.get("Id") != null) {res.put("Id", req.get("Id"));}
		switch(req.get("OP")) {
			case "REGISTER":			
				String REGISTER_Nickname = req.get("NK");
				Integer REGISTER_PIN = Integer.valueOf(req.get("PIN"));
				String REGISTER_Result = this.registerUser(REGISTER_Nickname, REGISTER_PIN);
				
				res.put("CODE", (REGISTER_Result.split(": ")[0] == "SUCCESS") ? "201" : "403");
				res.put("STATUS", REGISTER_Result.split(": ")[0]);
				res.put("MESSAGE", REGISTER_Result.split(": ")[1]);
				
				this.sendRequest(sender, res.toString());
				break;
			case "LOGIN":
				String LOGIN_Nickname = req.get("NK");
				Integer LOGIN_PIN = Integer.valueOf(req.get("PIN"));
				Boolean LOGIN_Success = this.loginUser(LOGIN_Nickname, LOGIN_PIN);
				
				res.put("CODE", (LOGIN_Success == true) ? "200" : "403");
				res.put("STATUS", (LOGIN_Success == true) ? "SUCCESS" : "ERROR");
				res.put("MESSAGE", "The credentials are correct!");
				
				this.sendRequest(sender, res.toString());
				break;
			case "RECOVER":
				String RECOVER_Nickname = req.get("NK");
				Integer RECOVER_PIN = this.recoverPIN(RECOVER_Nickname);
				
				res.put("CODE", (RECOVER_PIN != -1) ? "200" : "403");
				res.put("STATUS", (RECOVER_PIN != -1) ? "SUCCESS" : "ERROR");
				res.put("MESSAGE", (RECOVER_PIN != -1) ? "Your PIN is: " + RECOVER_PIN : "This user is not registered.");
				
				this.sendRequest(sender, res.toString());				
				break;
			case "SHOW_USERS":
				this.printUsers();
				break;
			default:
				System.out.println(req.get("OP") + ": " + "command not implemented.");
				break;
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

	public void printDebug(String message) {
		if(this.ShowDebug == true) {
			System.out.println("DEBUG: " + message);	
		}
		
	}
	
}

