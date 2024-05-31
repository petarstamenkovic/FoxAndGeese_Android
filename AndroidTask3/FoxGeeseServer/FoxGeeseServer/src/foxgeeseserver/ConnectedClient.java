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
    private boolean iAmFox; 
    private boolean iAmGeese;
    private boolean myTurn;
    private int[][] matrixGameBoard;
    private String foxPlayer;
    private String geesePlayer;
    private String geeseState;
      
    public ConnectedClient(Socket socket,ArrayList<ConnectedClient> allClients)
    {
        try {
            this.socket = socket;
            this.allClients = allClients;           
            this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream(),"UTF-8"));
            this.pw = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()),true);
            this.username = "";
            this.availlable = true;
            this.iAmFox = false;
            this.iAmGeese = false;
            this.myTurn = false;
            this.matrixGameBoard = new int[8][8];
            this.foxPlayer = "";
            this.geesePlayer = "";
            this.geeseState = "GEESE_SELECT";
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
                    System.out.println("Recieved username message, new user :" + newUsername);
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
                    System.out.println("Sending new list:" + allUsers);
                }
                
                // Got a challnge, forward it to the requested user
                if(line.startsWith("Challenge:"))
                {
                    String [] challengeToken = line.split(":");
                    String userToChallenge = challengeToken[1];
                    String challenger = challengeToken[2];
                    System.out.println("User:" + challenger + " challenged:" + userToChallenge);
                    if(!this.username.equals(userToChallenge))
                    {
                        for(ConnectedClient clnt : this.allClients)
                        {
                            if(clnt.username.equals(userToChallenge) && clnt.availlable)
                            {
                                clnt.pw.println("Recieved challenge - User :" + challenger + ":challenges:" + clnt.username);
                                break;
                            }
                        }
                    }
                    else
                    {
                        System.out.println("User challenged himself! This is not possible.");
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
                            clnt.pw.println("Declined user");
                            break;
                        }
                    }
                }
                
                // IF block that accepts a challenge and sends players in the new activity, divide roles and initialize the first players turn
                if(line.startsWith("Accepted challenge"))
                {
                    String [] acceptTokens = line.split(":");
                    String player1 = acceptTokens[1];
                    String player2 = acceptTokens[2];
                    this.foxPlayer = player1;
                    this.geesePlayer = player2;
                    
                    // Divide roles
                    if(this.username.equals(player1))
                    {
                        this.iAmFox = true;
                        this.iAmGeese = false;
                    }
                    else if(this.username.equals(player2))
                    {
                        this.iAmFox = false;
                        this.iAmGeese = true;
                    }
                    
                    if(this.iAmFox)
                        this.myTurn = true;
                    else
                        this.myTurn = false;
                    
                    for(ConnectedClient clnt : this.allClients)
                    {
                        if(clnt.username.equals(player1) || clnt.username.equals(player2))
                        {
                            clnt.availlable = false;
                            clnt.pw.println("Game on!");
                        }
                    }
                }
                
                // IF block that creates a matrix footprint of a map
                if(line.startsWith("FoxPostion"))
                {
                    String [] matrixToken = line.split(":");
                    String foxIndexPosition = matrixToken[1].trim();
                    int foxPosition = Integer.parseInt(foxIndexPosition);
                    // DARK FIELDS - 0 ; LIGHT FIELDS - 1 ; GEESE FIELDS - 2 ; FOX FIELD - 3 //
                    for(int row = 1 ; row <= 8 ; row++)
                    {
                        for(int col = 1; col <= 8 ; col++)
                        {
                            // Odd rows
                            if(row % 2 != 0)
                            {
                                if(col % 2 == 0)
                                {
                                    this.matrixGameBoard[row][col] = 1;
                                }
                                else
                                {
                                    this.matrixGameBoard[row][col] = 0;
                                }
                            }
                            // Even rows
                            else
                            {
                                if(col % 2 == 0)
                                {
                                    this.matrixGameBoard[row][col] = 0;
                                }
                                else
                                {
                                    this.matrixGameBoard[row][col] = 1;
                                }
                            }
                            
                            // Set geese position
                            if(row == 1 && col % 2 != 0)
                            {
                                this.matrixGameBoard[row][col] = 2;
                            }
                            
                            // Set fox position
                            if(row == 8 && col == foxPosition)
                            {
                                this.matrixGameBoard[row][col] = 3;
                            }
                        }
                    }
                    System.out.println("Matrix presentation of the board:" + this.matrixGameBoard);
                }
                
                // IF block that handles the new move
                if(line.startsWith("NewMove"))
                {
                    if(this.myTurn)
                    {
                        // Recieve and parse the move information
                        String [] moveToken = line.split(":");
                        String move = moveToken[1];
                        String [] positionToken = move.split(",");
                        String newRowIndex = positionToken[0];
                        String newColIndex = positionToken[1];
                        int newRow = Integer.parseInt(newRowIndex);
                        int newCol = Integer.parseInt(newColIndex);
                        int currRow = 0;
                        int currCol = 0;
                        
                        // Fox move handling
                        if(this.iAmFox)
                        {
                            boolean foundFox = false;
                            for(int i = 1 ; i <= 8 ; i++)
                            {
                                for(int j = 1 ; j <= 8 ; j++)
                                {
                                    if(this.matrixGameBoard[i][j] == 3)
                                    {
                                        currRow = i;
                                        currCol = j;
                                        foundFox = true;
                                        break;
                                    }
                                }
                                if(foundFox)
                                {
                                    break;
                                }
                                
                            }
                            
                            // Check if this is valid fox move
                            if(Math.abs(newRow - currRow) == 1 && Math.abs(newCol - currCol) == 1 && this.matrixGameBoard[newRow][newCol] == 0)
                            {
                                // Update the fields
                                this.matrixGameBoard[currRow][currCol] = 0;
                                this.matrixGameBoard[newRow][newCol] = 3;
                                this.myTurn = !this.myTurn; // Does this do the job? How to switch opponents turn?
                                for(ConnectedClient clnt : this.allClients)
                                {
                                    if(clnt.username.equals(this.foxPlayer) || clnt.username.equals(geesePlayer))
                                    {
                                        clnt.pw.println("UpdateBoardFox:"+newRow+":"+newCol);
                                        // How to switch moves?
                                    }
                                }
                                
                                // Is this possible?
                                for(ConnectedClient clnt : this.allClients)
                                {
                                    if(clnt.username.equals(this.geesePlayer))
                                    {
                                        clnt.myTurn = true;
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                this.pw.println("Invalid move");
                            }
                        }
                        // Geese move handling - Make a state machine (GEESE SELECT -> GEESE MOVE)
                        else if(this.iAmGeese)
                        {
                            int currGeeseRow;
                            int currGeeseCol;
                            switch(this.geeseState)
                            {
                                case "GEESE_SELECT":
                                    currGeeseRow = newRow;
                                    currGeeseCol = newCol;
                                    this.geeseState = "GEESE_MOVE";
                                    break;
                                    
                                case "GEESE_MOVE" : // Do the same checking like u did for fox only more limited (downwards)
                                    
                                    
                                    break;
                                    
                                
                            }
                            
                        }
                    }
                }
                
                /// NEW TYPE OF MESSAGE HERE ///
        }

    }
}