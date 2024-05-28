package foxgeeseserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ConnectedClient implements Runnable{
    
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private ArrayList<ConnectedClient> allClients;
    private String username;
    private boolean availlable;
      
    public ConnectedClient(Socket socket,ArrayList<ConnectedClient> allClients)
    {
        try {
            this.socket = socket;
            this.allClients = allClients;           
            this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream(),"UTF-8"));
            this.pw = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()),true);
            this.username = "";
            this.availlable = true;
        } catch (IOException ex) {
            Logger.getLogger(ConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Overriden method that runs upon creating a new client thread
    @Override
    public void run()
    {
        String line = "";
        while(true)
        {
            try {
                line = br.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
                // New user enters their username, modify the spinner
                if(line.startsWith("Name:"))
                {
                    
                    String [] userToken = line.split(":");
                    String newUsername = userToken[1];
                    this.username = newUsername;
                    String allUsers = "";
                    for(ConnectedClient clnt : this.allClients)
                    {
                        allUsers += clnt.username+",";
                        
                    }
                    
                    for(ConnectedClient clnt : this.allClients)
                    {
                        clnt.pw.println("NewList:" + allUsers);
                    }          
                }
                
                // Got a challnge, forward it to the requested user
                if(line.startsWith("Challenge:"))
                {
                    String [] challengeToken = line.split(":");
                    String userToChallenge = challengeToken[1];
                    String challenger = challengeToken[2];
                    for(ConnectedClient clnt : this.allClients)
                    {
                        if(clnt.username.equals(userToChallenge) && clnt.availlable)
                        {
                            clnt.pw.println("Recieved challenge - User :" + challenger + " challenges:" + clnt.username);
                            break;
                        }
                    }
                }
                
                // IF block that declines a challenge
                if(line.startsWith("Declined challenge"))
                {
                    String [] declineToken = line.split(":");
                    String declinedUser = declineToken[1];
                    for(ConnectedClient clnt : this.allClients)
                    {
                        if(clnt.username.equals(declinedUser))
                        {
                            clnt.pw.println("Declined user:" + declinedUser);
                            break;
                        }
                    }
                }
                
                if(line.startsWith("Accepted challenge"))
                {
                    String [] acceptTokens = line.split(":");
                    String player1 = acceptTokens[1];
                    String player2 = acceptTokens[2];
                    for(ConnectedClient clnt : this.allClients)
                    {
                        if(clnt.username.equals(player1) || clnt.username.equals(player2))
                        {
                            clnt.availlable = false;
                            clnt.pw.println("Game on!");
                        }
                    }
                }
                /// NEW TYPE OF MESSAGE HERE ///
        }

    }
}