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
        //catch timeout case, return whatever recieved so far 
        catch(SocketTimeoutException ex)
        {
            return serverOutput;
        }
        //if connection got lost
        catch(ConnectException ex)
        {
            return "Connection lost";
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