package chat_frontend;

import java.util.*;

public class Program {

	public static void main(String[] args) {
		// STEP #1: SET ADDRESS FOR REGISTER AGENT //
		Scanner in = new Scanner(System.in);
		System.out.print("Set address for Register Agent, in format IP:PORT => ");
		String RegisterAgent_Address = in.nextLine();
		
		System.out.print("Show DEBUG messages on terminal? [Y/n] => ");
		String ShowDebug = in.nextLine().toUpperCase();		
		
		in.close();
		// STEP #1: SET ADDRESS FOR REGISTER AGENT //
		
		// STEP #2: SHOW LOGIN SCREEN //		
		Login login = new Login();
		login.RegisterAgent_Address = RegisterAgent_Address;
		login.setVisible(true);
		login.setDebug((ShowDebug == "N") ? false : true);
		// STEP #2: SHOW LOGIN SCREEN //
		
		
		//Chat chat = new Chat();
		
		/*
		Scanner in = new Scanner(System.in);
		System.out.print("Set port for this equipment => ");
		String MyPort = in.nextLine();		

		Socket sock = new Socket(Integer.valueOf(MyPort));
		StartSocket(sock);	

		System.out.print("Set destination in format IP_ADDRESS:PORT => ");
		String[] Destination = in.nextLine().split(":");
	
		while(true) {
			System.out.print("Type message => ");
			String MyMessage = in.nextLine();
		
			sock.sendDatagramPacket(Integer.valueOf(Destination[1]), MyMessage, Destination[0]);
		
			if(MyMessage.equalsIgnoreCase("SHUTDOWN")){
				sock.DatagramSocket.close();
				System.exit(0);
			}
		
		}
		*/
	}
	
	public static void StartSocket(Socket sock){
		sock.start();
	}

}
