/* This program echos the HTTP request sent by the web browser and displays it as text output
 * it takes in a port as an argument and opens up a connection to that specific port.

@author Emil Stahl
*/ 

import java.net.*;
import java.io.*;

public class HTTPEcho {

	public static void main( String[] args) throws Exception {

		//store the clients data in the string toServer
		String toServer; 
		//open a serversocket with the port given by the user
		ServerSocket HTTPSocket = new ServerSocket(Integer.parseInt(args[0])); 
		//Header to be appended first
		String Header = "HTTP/1.1 200 OK\r\n\r\n";
		
		while(true) {
			
			//Create socket and accept client
			Socket connection = HTTPSocket.accept();
			//open bufferreader and outputstream 
			BufferedReader clientInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			DataOutputStream clientOutput = new DataOutputStream(connection.getOutputStream());
			//The echo from server that is being returned to the client
			StringBuilder Echo = new StringBuilder();
			//The echo needs to begin with the http header
			Echo.append(Header);
			
			//while there's input, continue reading 
			while(((toServer = clientInput.readLine()) != null) && (toServer.length() != 0))
			{
				Echo.append(toServer + "\r\n"); //append it to Echo
			}
			
			//return the input as an echo
			clientOutput.writeBytes(Echo.toString());
			
			//close the sockets and streams
			clientInput.close();
			clientOutput.close();
			connection.close();
				
			}    	
    	}
	}

