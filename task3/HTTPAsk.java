import java.net.*;
import java.io.*;

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
        serverOutput = askServer(hostname, port, serverInput);
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

  public static String askServer(String hostname, int port, String ToServer) throws IOException
  {

    String serverOutput = "TEST";
    String fromServer;
    String firstLine;
    int i = 0;

    try
    {
      Socket clientSocket = new Socket(hostname, port);
      clientSocket.setKeepAlive(true);
      clientSocket.setSoTimeout(3000);

      DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
      BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      outToServer.writeBytes(ToServer + '\n');

      StringBuilder builder = new StringBuilder();

      firstLine = inFromServer.readLine();
      System.out.println(hostname + port);
      serverOutput = firstLine;
      System.out.println(firstLine);

      builder.append(firstLine + '\n');

      while(((fromServer = inFromServer.readLine()) != null) && (fromServer.length() != 0))
      {
        System.out.println(i);
        builder.append(fromServer + '\n');

        i++;

        if(i > 50)
        {
          break;
        }
      }

      serverOutput = builder.toString();

      outToServer.close();
      inFromServer.close();
      clientSocket.close();

      return serverOutput;
    }
    catch(SocketTimeoutException ex)
    {
      System.err.println(ex);
      return serverOutput;
    }
    catch(ConnectException ex)
    {
      System.err.println(ex);
      return "Package discarded";
    }
    catch(UnknownHostException ex)
    {
      System.err.println(ex);
      if(hostname.equals("HTTP/1.1"))
      {
        return "HTTP/1.1 400 Bad Request\r\n\r\n";
      }
      return "HTTP/1.1 404 Not Found\r\n\r\n";
    }
    catch(SocketException ex)
    {
      System.err.println(ex);
      return serverOutput;
      //return "HTTP/1.1 404 Not Found\r\n\r\n";
    }
  }

  public static String askServer(String hostname, int port) throws IOException
  {
    String serverOutput;

    serverOutput = askServer(hostname, port, null);

    return serverOutput;
  }
}