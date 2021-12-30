package nameserver;

public class User {
	private String Username;
	private Integer PIN;
	
	public User(String Username, Integer PIN) {
		this.Username = Username; //8 caracteres
		this.PIN = PIN;
	}
	
	public String getUsername() {
		return this.Username;
	}
	
	public Integer getPIN() {
		return this.PIN;
	}
	
	public String toString() {
		String Placeholder = "{ \"Username\" : {0}, \"PIN\" : {1} }";
		return Placeholder.replaceFirst("{0}", this.getUsername()).replaceFirst("{1}", String.valueOf(this.getPIN()));
	}
}
