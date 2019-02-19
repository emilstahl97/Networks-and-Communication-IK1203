import java.net.*;
import java.io.*;

public class HTTPEcho {

	public static void main( String[] args) throws Exception {

		String toServer; 
		String Echo;
		int port = Integer.parseInt(args[0]);
		ServerSocket HTTPSocket = new ServerSocket(port); 
		
		while(true) {
			
			Socket connection = HTTPSocket.accept();
			BufferedReader clientInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			DataOutputStream clientOutput = new DataOutputStream(connection.getOutputStream());
			StringBuilder sb = new StringBuilder();
			boolean statusLine = true; 
			
			while(((toServer = clientInput.readLine()) != null) && (toServer.length() != 0))
			{
				if(statusLine) 
				{
					sb.append("HTTP/1.1 200 OK\r\n\r\n");
					statusLine = false;
				}
				sb.append(toServer + "\r\n");
			}
			
			Echo = sb.toString();
			clientOutput.writeBytes(Echo);
			
			clientInput.close();
			clientOutput.close();
			connection.close();
				
			}    	
    	}
	}

