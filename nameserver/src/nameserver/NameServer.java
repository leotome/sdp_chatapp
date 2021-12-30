package nameserver;

import java.util.*;

public class NameServer {
	
	private Integer NS_Port;
	private List<User> Users;
	
	public NameServer(Integer myPort) {
		this.NS_Port = myPort;
		this.Users = new ArrayList<User>();
	}
	
	public String registerUser(String Username, Integer PIN) {
		Boolean HasUsername = this.checkUsername(Username);
		Boolean HasPIN = this.checkPIN(PIN);
		if(HasUsername == true) {
			return "Oops, utilizador já existe. Por favor escolha outro.";
		} else if (HasPIN == true) {
			return "Oops, PIN já registado a outro utilizador. Por favor escolha outro.";
		}
		User NewUser = new User(Username, PIN);
		Users.add(NewUser);
		return "{0} foi registado com sucesso.".replaceFirst("{0}", Username);
	}
	
	public boolean checkUsername(String Username) {
		for(User user : Users) {
			Boolean UsernameIsEqual = user.getUsername() == Username;
			if(UsernameIsEqual == true) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkPIN(Integer PIN) {
		for(User user : Users) {
			Boolean PINIsEqual = user.getPIN() == PIN;
			if(PINIsEqual == true) {
				return true;
			}
		}
		return false;
	}
	
	public User queryUser(String Username) {
		for(User user : Users) {
			Boolean UsernameIsEqual = user.getUsername() == Username;
			if(UsernameIsEqual == true) {
				return user;
			}
		}
		return null;
	}
	
	public String printUsers() {
		String payload = "";
		payload += "************************" + "\n";
		payload += "** Username ***  PIN  **" + "\n";
		String eachUserTemplate = "** xxxxxxxx *** yyyy **";
		for(User user : Users) {
			if(user.getUsername().length() == 8) {
				payload += eachUserTemplate.replaceFirst("xxxxxxxx", user.getUsername()).replaceFirst("yyyy", String.valueOf(user.getPIN()));
			} else {
				Integer missing = 8 - user.getUsername().length();
				String formattedUsername = user.getUsername();
				for(Integer i = 0 ; i < missing ; i++) {
					formattedUsername += " ";
				}
				payload += eachUserTemplate.replaceFirst("xxxxxxxx", formattedUsername).replaceFirst("yyyy", String.valueOf(user.getPIN()));
			}
			payload += "\n";
		}
		payload += "************************";
		return payload;
	}
	
	

}

