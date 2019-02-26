import java.net.*;
import java.io.*;
import tcpclient.TCPClient;

public class HTTPAsk
{
  public static void main(String[] args) throws IOException
  {

    int port;
    try
    {
      port = Integer.parseInt(args[0]);
    }
    catch (Exception ex)
    {
      System.err.println("Usage: HTTPAsk port");
      return;
    }

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


	ServerSocket HTTPSocket = new ServerSocket(port); 

    while(true)
    {
	  
      //Create socket and accept client
      Socket connection = HTTPSocket.accept();
      connection.setSoTimeout(3000);

      //open bufferreader and outputstream 
	  BufferedReader clientInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	  DataOutputStream clientOutput = new DataOutputStream(connection.getOutputStream());
	  //The echo from server that is being returned to the client
	  StringBuilder response = new StringBuilder();
	boolean statusLineCheck = true;

      while(((clientSentence = clientInput.readLine()) != null) && (clientSentence.length() != 0))
      {
        if(statusLineCheck)
        {
          System.out.println(clientSentence);
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
          System.out.println(length);

          if(length > 2)
          {
            hostname = subString[2];
            System.out.println(hostname);
          }

          if(length > 4)
          {
            port = Integer.parseInt(subString[4]);
            System.out.println(port);
          }

          if(length > 6)
          {
            serverInput = subString[6];
            System.out.println(serverInput);
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
      catch(IOException error)
      {
        System.err.println(error);
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
  }
}