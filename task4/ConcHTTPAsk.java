/* This program is a concurrent server that creates a new thread for each request it gets.
 *
 * @author Emil Stahll
 * Date January 28th 2019
 */


import java.net.*;
import java.io.*;

public class ConcHTTPAsk {

	
	public static void main(String[] args) throws IOException {
		
		//take in port as argument, 8888 as default
	    int port;
	    if(args.length > 0)
	    	port = Integer.parseInt(args[0]);
	    else port = 8888; //set default

	    try {
	    
	    //create serversocket
	    ServerSocket HTTPSocket = new ServerSocket(port);

	    while(true)
	    {
	      Socket connection = HTTPSocket.accept();
	      MyRunnable r = new MyRunnable(connection); //create MyRunnable object with socket
	      new Thread(r).start(); //create thread for each request
	    }
	    }
	    catch(IOException error) {
	    	System.err.println(error);
	    }
	  }
	}
