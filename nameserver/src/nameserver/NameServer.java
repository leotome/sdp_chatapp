package nameserver;

import java.util.*;

public class NameServer {

	private Socket socket;
	private Map<String, User> Users;
	private List<Integer> AllowedPINs;
	
	public Boolean ShowDebug = false;

	public NameServer() {
		this.Users = new HashMap<String, User>();
		this.setPINPolicy();
	}
	
	public Socket getSocket() {
		return this.socket;
	}	
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void setPINPolicy() {
		List<Integer> AllowedPINs = new ArrayList<Integer>();
		for(Integer i = 8000; i <= 8010; i++) {
			AllowedPINs.add(i);
		}
		this.AllowedPINs = AllowedPINs;
	}
	
	public String registerUser(String Address, String Nickname, Integer PIN) {
		if(this.Users.containsKey(Nickname) == true) {
			return "ERROR: This user already exists";
		}
		for(User user : this.Users.values()) {
			if(user.PIN.equals(PIN)) {
				return "ERROR: This PIN is being used";	
			}
		}
		if(AllowedPINs.contains(PIN) == false) {
			return "ERROR: PIN does not match policy.";
		}
		User user = new User();
		user.Nickname = Nickname;
		user.PIN = PIN;
		user.Address = Address;
		this.Users.put(Nickname, user);
		printDebug("User '" + Nickname + "' was registered sucessfully with PIN '" + PIN + "'");
		return "SUCCESS: The user was created successfully.";
	}
	
	public boolean loginUser(String Nickname, Integer PIN) {
		if(this.Users.containsKey(Nickname)) {
			Integer PIN_User = this.Users.get(Nickname).PIN;
			return PIN_User.equals(PIN);
		}
		return false;
	}	
	
	public Integer recoverPIN(String Nickname) {
		printDebug("Requested PIN for User '" + Nickname + "'");
		if(this.Users.containsKey(Nickname)) {
			return this.Users.get(Nickname).PIN;
		}
		return -1;
	}
	
	public Map<String, String> resolveNicknames(String[] CSV_Nicknames){
		Map<String, String> result = new HashMap<String, String>();
		Integer CounterAll = 0;
		Integer CounterSuccess = 0;
		for(String RequestedNickname : CSV_Nicknames) {
			CounterAll++;
			result.put("ITEM_USER-" + CounterAll, RequestedNickname);
			if(this.Users.get(RequestedNickname) != null) {
				result.put("ITEM_STATUS-" + CounterAll, "SUCCESS");
				result.put("ITEM_ADDRESS-" + CounterAll, this.Users.get(RequestedNickname).Address + ":" + String.valueOf(this.Users.get(RequestedNickname).PIN));
				CounterSuccess++;
			} else {
				result.put("ITEM_STATUS-" + CounterAll, "ERROR");
			}
		}
		result.put("SIZEALL", String.valueOf(CounterAll));
		result.put("SUCCESS", String.valueOf(CounterSuccess));
		return result;
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
		this.getSocket().sendDatagramPacket(Integer.valueOf(ToAddress[1]), payload, ToAddress[0]);
	}
	
	public void handleRequest(String sender, String request) {
		printDebug("Received DATAGRAM from '" + sender + "', with PAYLOAD '" + request + "'");
		Map<String, String> req = this.formatRequest(request);
		Map<String, String> res = new HashMap<String, String>();
		res.put("APP", "NS");
		if(req.get("Id") != null) {res.put("Id", req.get("Id"));}
		switch(req.get("OP")) {
			case "REGISTER":
				String REGISTER_Address = req.get("ADDRESS").split(":")[0];
				String REGISTER_Nickname = req.get("NK");
				Integer REGISTER_PIN = Integer.valueOf(req.get("PIN"));
				String REGISTER_Result = this.registerUser(REGISTER_Address, REGISTER_Nickname, REGISTER_PIN);
				
				res.put("TYPE", "REGISTER");
				res.put("CODE", (REGISTER_Result.split(": ")[0] == "SUCCESS") ? "201" : "403");
				res.put("STATUS", REGISTER_Result.split(": ")[0]);
				res.put("MESSAGE", REGISTER_Result.split(": ")[1]);
				res.put("NK", req.get("NK"));
				res.put("PIN", req.get("PIN"));
				
				this.sendRequest(sender, res.toString());
				break;
			case "LOGIN":
				String LOGIN_Nickname = req.get("NK");
				Integer LOGIN_PIN = Integer.valueOf(req.get("PIN"));
				Boolean LOGIN_Success = this.loginUser(LOGIN_Nickname, LOGIN_PIN);
				
				res.put("TYPE", "LOGIN");
				res.put("CODE", (LOGIN_Success == true) ? "200" : "403");
				res.put("STATUS", (LOGIN_Success == true) ? "SUCCESS" : "ERROR");
				res.put("MESSAGE", (LOGIN_Success == true) ? "The credentials are correct!" : "Incorrect credentials or user is not registered.");
				res.put("NK", req.get("NK"));
				res.put("PIN", req.get("PIN"));				
				
				this.sendRequest(sender, res.toString());
				break;
			case "RECOVER":
				String RECOVER_Nickname = req.get("NK");
				Integer RECOVER_PIN = this.recoverPIN(RECOVER_Nickname);
				
				res.put("TYPE", "RECOVER");
				res.put("CODE", (RECOVER_PIN != -1) ? "200" : "403");
				res.put("STATUS", (RECOVER_PIN != -1) ? "SUCCESS" : "ERROR");
				res.put("MESSAGE", (RECOVER_PIN != -1) ? "Your PIN is: " + RECOVER_PIN : "This user is not registered.");
				
				this.sendRequest(sender, res.toString());				
				break;
			case "RESOLVE":
				String RESOLVE_Nicknames = req.get("NK");
				res.put("TYPE", "RESOLVE");
				
				Map<String, String> RESOLVE_result = this.resolveNicknames(RESOLVE_Nicknames.split(";"));
				for(String key : RESOLVE_result.keySet()) {
					res.put(key, RESOLVE_result.get(key));
				}
				
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

