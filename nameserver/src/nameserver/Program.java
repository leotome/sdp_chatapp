package nameserver;

import java.util.*;

public class Program {

	public static void main(String[] args) {
		NameServer nameserver = null;
		System.out.println("Welcome to NameServer. Type HELP to see supported commands.");
		Scanner input = new Scanner(System.in);
		while(input.hasNextLine()) {
			String line = input.nextLine();
			if(line.isBlank()) {
				input.close();
				System.exit(0);
			}
			String[] commands = line.split(" ");
			switch(commands[0]) {
			case "HELP":
				System.out.println("START     " + " => " + "Starts the nameserver. Syntax is START [A] [B], where [A] is the port for nameserver, [B] is register agent address in format IP:PORT");
				System.out.println("STATUS    " + " => " + "If the server is running, returns the port allocated to the nameserver. Returns an error otherwise.");
				System.out.println("RECOVER   " + " => " + "Prints the PIN given an username. Syntax is RECOVER [A], where [A] is the nickname.");
				System.out.println("SHOW_USERS" + " => " + "Prints all users.");
				System.out.println("SHUTDOWN  " + " => " + "Destroy the server.");
				break;
			case "START":
				if(commands.length == 3) {
					if(nameserver != null) {
						System.out.println("The service is already started. Please review.");
					} else {
						Integer NSPort = Integer.valueOf(commands[1]);
						String RGAddress = commands[2];
						nameserver = new NameServer(NSPort, RGAddress);
						Socket sock = new Socket(NSPort, nameserver);
						StartSocket(sock);	
						
						System.out.println("The service was started successfully!");
					}
					
				} else {
					System.out.println(commands[0] + ": " + "command not found");
				}
				break;
			case "STATUS":
				if(commands.length == 1) {
					if(nameserver != null) {
						System.out.println("Port: " + nameserver.getSocket().PORT);
					} else {
						System.out.println("The service is not started.");
					}
				} else {
					System.out.println(commands[0] + ": " + "command not found");
				}
				break;
			case "RECOVER":
				if(commands.length == 2) {
					System.out.println(nameserver.recoverPIN(commands[1]));			
				} else {
					System.out.println(commands[0] + ": " + "command not found");
				}
				break;
			case "SHOW_USERS":
				if(commands.length == 1) {
					if(nameserver != null) {
						nameserver.printUsers();
					} else {
						System.out.println("The service is not started.");
					}

				} else {
					System.out.println(line + ": " + "command not found");
				}
				break;
			case "SHUTDOWN":
				if(commands.length == 1) {
					System.exit(0);
				} else {
					System.out.println(line + ": " + "command not found");
				}
				break;
			default:
				System.out.println(commands[0] + ": " + "command not found");
			}
		}
		input.close();
	}
	
	public static void StartSocket(Socket sock){
		sock.start();
	}	
	
}
