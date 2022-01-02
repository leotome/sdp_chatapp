package chat_frontend;

import java.net.*;

public class Socket extends Thread {
	InetAddress Sender;					// IP Address object for "Sender"
	InetAddress Destination;			// IP Address object for "Destination", or "Recipient"
	DatagramSocket DatagramSocket;		// DatagramSocket, which is a UDP Socket
	byte[] Buffer = new byte[1024];		// Local buffer, for holding the incoming datagram
	Integer PORT;						// Port for the Socket
	
	private Boolean Destroy = false;
	
	Login login;
	Chat chat;
	
	public Socket(Integer PORT) {		// Constructor, receiving "PORT" as an argument 
		this.PORT = PORT;
	}
	
	public Socket() {					// Another version for the constructor, considering NO arguments
	}
	
	public void run() {
		try {											  
			DatagramSocket = null;
			if(PORT != null) {                             // Initializing DatagramSocket to bound a UDP Socket
				DatagramSocket = new DatagramSocket(PORT); // #1: It uses PORT variable to define the Socket PORT
			}                                              // #2: Accepts datagrams from all IPv4 addresses on this computer
			if(PORT == null) {                             // Initializing DatagramSocket WITHOUT PORT
				DatagramSocket = new DatagramSocket(); 	   // #1: It uses any available PORT to send the request.
			}                                              // #2: Accepts datagrams from all IPv4 addresses on this computer
		} catch(Exception e){}
		while(Destroy == false) {
			receiveDatagramPacket();
		}
	}
	
	public void receiveDatagramPacket(){
		try {
			DatagramPacket DatagramPacket = new DatagramPacket(Buffer, Buffer.length);
			DatagramSocket.receive(DatagramPacket);
			Sender = DatagramPacket.getAddress();
			String Message = new String(DatagramPacket.getData(), 0, DatagramPacket.getLength());
			String Sender_IP = Sender.toString().substring(1);
			if(login != null) {
				login.handleRequest(Sender_IP, Message);
			} else if (chat != null) {
				chat.handleRequest(Sender_IP, Message);
			}
		} catch (Exception e){}
	}
	
	public void sendDatagramPacket(int Port, String Message, String Recipient){
		try{
			Destination = InetAddress.getByName(Recipient);
			DatagramPacket DatagramPacket = new DatagramPacket(Message.getBytes(), Message.getBytes().length, Destination, Port);
			DatagramSocket.send(DatagramPacket);
			
		}catch(Exception e){}
	}
	
	public void destroy() {
		this.Destroy = true;
	}

}
