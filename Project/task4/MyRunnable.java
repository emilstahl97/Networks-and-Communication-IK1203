// Class for MyRunnable object
// @author Emil Stahl
// Date: January 28th 2019

import java.net.*;
import java.io.*;
import tcpclient.TCPClient;

public class MyRunnable implements Runnable {

  Socket connection;
  int port;

  public MyRunnable(Socket connection) {
    this.connection = connection;
  }
  public void run()
  {
    try
    {
    	
      String clientSentence;
      String[] subString;
      String splitString;
      String hostname = "localhost";
      String serverInput = null;
      String serverOutput = null;
      String serverResponse;
      String statusLine = null;
      String HTTP404 = "HTTP/1.1 404 Not Found\r\n\r\n"; 
      String HTTP200 = "HTTP/1.1 200 OK\r\n\r\n";
      String HTTP400 = "HTTP/1.1 400 Bad Request\r\n\r\n";

      //open bufferreader and outputstream 
      BufferedReader clientInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      DataOutputStream clientOutput = new DataOutputStream(connection.getOutputStream());
	  //The response from server that is being returned to the web browser
      StringBuilder response = new StringBuilder();
      boolean statusLineCheck = true;

	  //while there's input, continue reading 
      while(((clientSentence = clientInput.readLine()) != null) && (clientSentence.length() != 0))
      {
        if(statusLineCheck)
        {
          System.out.println("Client sentence is:\n" + clientSentence);
          splitString = clientSentence;
          subString = splitString.split(" |=|&");

          if(subString[1].matches("(?i).*/favicon.ico.*"))
          {
            statusLine = HTTP404;
          }
          else if((subString[0].equals("GET")) || (subString[1].matches("(?i).*/ask?.*")))
          {
            statusLine = HTTP200;
          }
          else
          {
            statusLine = HTTP400;
          }

          int length = subString.length;
          System.out.println("Length of client sentence array is:\n" + length);

          if(length > 2)
          {
            hostname = subString[2];
            System.out.println("Hostname is:\n" + hostname);
          }

          if(length > 4)
          {
            port = Integer.parseInt(subString[4]);
            System.out.println("Port is:\n" + port);
          }

          if(length > 6)
          {
            serverInput = subString[6];
            System.out.println("Server input is:\n" + serverInput);
          }
        }

        statusLineCheck = false;
      }

      try
      {
        serverOutput = TCPClient.askServer(hostname, port, serverInput);
        if((serverOutput.equals(HTTP404.toString())) || (serverOutput.equals(HTTP400.toString())))
        {
          statusLine = serverOutput;
        }
      }
      catch(IOException ex)
      {
        System.err.println(ex);
      }

      response.append(statusLine);
      response.append(serverOutput + "\r\n");
      serverResponse = response.toString();
      System.out.println(serverResponse);
      clientOutput.writeBytes(serverResponse);

      clientInput.close();
      clientOutput.close();
      connection.close();
    }
    catch(IOException ex)
    {
      System.err.println(ex);
    }
  }
}