/* This program takes in a hostname, port and a string and open up a connection to the given server.
 * If the server does not close the connection the program will do so by itself after 3000 ms.
 
 @emilstahl
 
 */

package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient
{

    public static String askServer(String hostname, int port, String ToServer) throws  IOException
    {

        String serverOutput = null;

        String fromServer, response;

        //counter too keep track of number of lines from server
        int c = 0;

        try
        {

            Socket clientSocket = new Socket(hostname, port);

            clientSocket.setSoTimeout(3000); // Timeout if no response in 3 seconds

            // Create input and output streams
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //send the given string to outputstream
            outToServer.writeBytes(ToServer + '\n');

            StringBuilder stringInput = new StringBuilder();

            response = inFromServer.readLine();

            serverOutput = response;

            stringInput.append(response + '\n');

            //keep reading while there's more input
            while((fromServer = inFromServer.readLine()) != null)
            {
                stringInput.append(fromServer + '\n');

                c++;

                if(c > 50)
                {
                    c = 0;
                    break;
                }
            }
            serverOutput = stringInput.toString();

            //close outputstream
            outToServer.close();
            //close clientsocket
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

    //instead of creating a new method with the same code
    public static String askServer(String hostname, int port) throws  IOException //overloaded function
    {
        String serverOutput;

        serverOutput = askServer(hostname, port, null); //calls the function above with null as string

        return serverOutput;
    }
}