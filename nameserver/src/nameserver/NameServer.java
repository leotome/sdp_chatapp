package nameserver;

import java.util.*;

public class NameServer {

	private Socket socket;
	private Map<String, Integer> Users;
	private Integer NameServer_Port;
	private String RegisterAgent_Address;

	public NameServer(Integer NameServer_Port, String RegisterAgent_Address) {
		this.NameServer_Port = NameServer_Port;
		this.RegisterAgent_Address = RegisterAgent_Address;
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
	
	public void registerUser(String Nickname, Integer PIN) {
		this.Users.put(Nickname, PIN);
		printDebug("User '" + Nickname + "' was registered sucessfully with PIN '" + PIN + "'");
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
	
	public void handleRequest(String sender, String request) {
		printDebug(sender);
		printDebug(request);
		Map<String, String> response = this.formatRequest(request);
		switch(response.get("OP")) {
			case "REGISTER":
				String REGISTER_Nickname = response.get("NK");
				Integer REGISTER_PIN = Integer.valueOf(response.get("PW"));
				this.registerUser(REGISTER_Nickname, REGISTER_PIN);
				break;
			case "LOGIN":
				String LOGIN_Nickname = response.get("NK");
				Integer LOGIN_PIN = Integer.valueOf(response.get("PW"));
				this.loginUser(LOGIN_Nickname, LOGIN_PIN);
				break;
			case "RECOVER":
				String RECOVER_Nickname = response.get("NK");
				this.recoverPIN(RECOVER_Nickname);
				break;
			default:
				break;
		}

	}
	
	public Map<String, String> formatRequest(String request){
		Map<String, String> response = new HashMap<String, String>();
		String[] splitted = request.replace("{", "").replace("}", "").split(",");
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

