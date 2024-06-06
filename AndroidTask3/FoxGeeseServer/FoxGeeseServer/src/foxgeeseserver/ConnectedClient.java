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
    private int currentGeeseRow;
    private int currentGeeseCol;
    private int currentFoxRow;
    private int curretnFoxCol;

      
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

    // Prints out the board in console
    public void printBoardMatrix()
    {
        // Print the matrix in a readable format
        System.out.println("Initial matrix presentation of the board:");
        for (int i = 0; i < matrixGameBoard.length; i++)
        {
            for (int j = 0; j < matrixGameBoard[i].length; j++)
            {
                System.out.print(matrixGameBoard[i][j] + " ");
            }
            System.out.println(); // Move to the next line after printing each row
        }
    }
    
    // Function that checks if geese have won
    public boolean didGeeseWin()
    {
        // Check when fox is on the left edge of the board
        if(this.curretnFoxCol == 0)
        {
            if((this.matrixGameBoard[this.currentFoxRow-1][this.currentGeeseCol+1] != 0) && (this.matrixGameBoard[this.currentFoxRow+1][this.curretnFoxCol+1] != 0))
            {
                return true;
            }
            else 
                return false;
        }
        // Chech when the fox is on the right edge of the board
        else if(this.currentGeeseCol == 7)
        {
            if((this.matrixGameBoard[this.currentFoxRow-1][this.currentGeeseCol-1] != 0) && (this.matrixGameBoard[this.currentFoxRow+1][this.currentGeeseCol-1] != 0))
            {
                return true;
            }
            else 
                return false;
        }
        else
        {
            if((this.matrixGameBoard[this.currentFoxRow-1][this.curretnFoxCol-1] != 0) && (this.matrixGameBoard[this.currentFoxRow+1][this.curretnFoxCol-1] != 0) && (this.matrixGameBoard[this.currentFoxRow-1][this.curretnFoxCol+1] != 0) && (this.matrixGameBoard[this.currentFoxRow+1][this.curretnFoxCol+1] != 0))
            {
                return true;
            }
            else
                return false;
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
                    System.out.println("Fox is : " + player1 + " and geese is: "+ player2);
                    
                    // Update roles for both players
                    for(ConnectedClient clnt : this.allClients)
                    {
                        if(clnt.username.equals(player1) || clnt.username.equals(player2))
                        {
                            clnt.foxPlayer = player1;
                            clnt.geesePlayer = player2;
                        }
                    }
                    
                    // This is geese
                    if(this.username.equals(player2))
                    {
                        this.myTurn = false;
                        this.iAmGeese = true;
                        this.iAmFox = false;
                    }
                    
                    // This is fox
                    for(ConnectedClient clnt : this.allClients)
                    {
                        if(clnt.username.equals(player1))
                        {
                            clnt.myTurn = true;
                            clnt.iAmFox = true;
                            clnt.iAmGeese = false;
                        }
                    }
                    
                    // Both players are now unavaillable
                    for(ConnectedClient clnt : this.allClients)
                    {
                        if(clnt.username.equals(player1) || clnt.username.equals(player2))
                        {
                            clnt.availlable = false;
                            clnt.pw.println("Game on!");
                        }
                    }
                }
                
                // THIS IS NOT RECIEVED //
                
                // IF block that creates a matrix footprint of a map
                if(line.startsWith("FoxPosition"))
                {
                    String [] matrixToken = line.split(":");
                    String foxIndexPosition = matrixToken[1].trim();
                    int foxPosition = Integer.parseInt(foxIndexPosition);
                    // DARK FIELDS - 0 ; LIGHT FIELDS - 1 ; GEESE FIELDS - 2 ; FOX FIELD - 3 //
                    for(int row = 0 ; row <= 7 ; row++)
                    {
                        for(int col = 0; col <= 7 ; col++)
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
                            if(row == 0 && col % 2 == 0)
                            {
                                this.matrixGameBoard[row][col] = 2;
                            }
                            
                            // Set fox position
                            if(row == 7 && col == foxPosition)
                            {
                                this.matrixGameBoard[row][col] = 3;
                                for(ConnectedClient clnt : this.allClients)
                                {
                                    if(clnt.username.equals(this.foxPlayer) || clnt.username.equals(this.geesePlayer))
                                    {
                                        clnt.currentFoxRow = row;
                                        clnt.curretnFoxCol = col;
                                    }
                                }
                            }
                        }
                    }
                    printBoardMatrix();
                }
                
                ////////// COMMANDS THAT MODIFY THE GAME ACTIVITY ///////////////
                
                // IF block that handles the new move
                if(line.startsWith("NewMove"))
                {
                    if(this.myTurn)
                    {
                        String gameState = "";
                        // Recieve and parse the move information
                        String [] moveToken = line.split(":");
                        String move = moveToken[1];
                        String [] positionToken = move.split(",");
                        String newRowIndex = positionToken[0];
                        String newColIndex = positionToken[1];
                        
                        // New coordinates
                        int newRow = Integer.parseInt(newRowIndex);
                        int newCol = Integer.parseInt(newColIndex);
                        
                        // Variables to store the coordinates to be removed on GUI
                        int oldRow = 0;
                        int oldCol = 0;
                        
                        // Fox move handling
                        if(this.iAmFox)
                        {
                            // Fox move is valid - Update both players fox position
                            if(Math.abs(newRow - this.currentFoxRow) == 1 && Math.abs(newCol - this.curretnFoxCol) == 1 && this.matrixGameBoard[newRow][newCol] == 0)
                            {
                                //System.out.println("Fox played a valid move!");
                                for(ConnectedClient clnt : this.allClients)
                                {
                                    if(clnt.username.equals(this.foxPlayer) || clnt.username.equals(this.geesePlayer))
                                    {
                                        clnt.matrixGameBoard[this.currentFoxRow][this.curretnFoxCol] = 0;
                                        clnt.matrixGameBoard[newRow][newCol] = 3;
                                        oldRow = this.currentFoxRow;
                                        oldCol = this.currentGeeseCol;
                                        clnt.currentFoxRow = newRow;
                                        clnt.curretnFoxCol = newCol;
                                    }
                                }
                               
                                // Possible scenarios after a valid fox move
                                if(this.currentFoxRow == 0)
                                {
                                    gameState = "FoxWin";
                                }// Fix the borders here
                                else if(didGeeseWin())
                                {
                                    gameState = "GeeseWin";
                                }
                                else
                                {
                                    gameState = "FoxUpdate";
                                }
                                System.out.println("Game state is :" + gameState);
                                // Send both players message about new game status
                                for(ConnectedClient clnt : this.allClients)
                                {
                                    if(clnt.username.equals(this.foxPlayer) || clnt.username.equals(geesePlayer))
                                    {
                                        switch(gameState)
                                        {
                                            case "FoxWin" : 
                                                System.out.println("Fox wins!");
                                                clnt.pw.println("FoxWin:"+oldRow + ":" + oldCol + ":" +newRow+":"+newCol);
                                                break;
                                            case "GeeseWin" :     
                                                System.out.println("Geese wins!");
                                                clnt.pw.println("GeeseWin:"+oldRow + ":" + oldCol + ":" +newRow+":"+newCol);
                                                break;
                                            case "FoxUpdate": 
                                                System.out.println("Fox move valid, sending a request to update GUI!");
                                                printBoardMatrix();
                                                clnt.pw.println("UpdateBoardFox:"+oldRow + ":" + oldCol + ":" +newRow+":"+newCol);
                                                break;
                                        }
                                    }
                                }
                                
                                // Move handling logic - Fox player turn false, geese player turn true
                                this.myTurn = !this.myTurn;
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
                                System.out.println("Invalid fox move!");
                                this.pw.println("InvalidFoxMove");
                            }
                        }
                        // Geese move handling - Make a state machine (GEESE SELECT -> GEESE MOVE)
                        else if(this.iAmGeese)
                        {
                            switch(this.geeseState)
                            {
                                case "GEESE_SELECT":
                                    if(this.matrixGameBoard[newRow][newCol] == 2)
                                    {
                                        this.currentGeeseRow = newRow;
                                        this.currentGeeseCol = newCol;
                                        this.geeseState = "GEESE_MOVE";
                                        this.pw.println("GeeseSelectedOk:" + this.currentGeeseRow + ":" + this.currentGeeseCol);
                                        System.out.println("You selected geese succesfully! Now pick a valid field.");
                                    }
                                    else
                                    {
                                        System.out.println("You did not select a geese! Geese is green circle.");
                                        this.pw.println("DidntSelectGeese");
                                        this.geeseState = "GEESE_SELECT";
                                    }
                                    
                                    break;
                                    
                                case "GEESE_MOVE" :
                                    // Valid geese move
                                    if((Math.abs(newCol - this.currentGeeseCol) == 1) && (Math.abs(newRow - this.currentGeeseRow) == 1) && (newRow > this.currentGeeseRow) && this.matrixGameBoard[newRow][newCol] == 0)
                                    {           
                                        // Update the server board
                                        for(ConnectedClient clnt : this.allClients)
                                        {
                                            if(clnt.username.equals(this.foxPlayer) || clnt.username.equals(this.geesePlayer))
                                            {
                                                clnt.matrixGameBoard[this.currentGeeseRow][this.currentGeeseCol] = 0;
                                                clnt.matrixGameBoard[newRow][newCol] = 2;
                                            }
                                        }
                                        
                                        // Possible scenarios after a valid geese move
                                        if(didGeeseWin())
                                        {
                                            gameState = "GeeseWin";
                                        }
                                        else
                                        {
                                            gameState = "UpdateBoardGeese";
                                        }

                                        // Send both players message about new game status
                                        for(ConnectedClient clnt : this.allClients)
                                        {
                                            if(clnt.username.equals(this.foxPlayer) || clnt.username.equals(geesePlayer))
                                            {
                                                switch(gameState)
                                                {
                                                    case "GeeseWin" : 
                                                        System.out.println("Geese win!");
                                                        clnt.pw.println("GeeseWin:"+this.currentGeeseRow+":" + this.currentGeeseCol +":"+newRow+":"+newCol);
                                                        break;
                                                    case "UpdateBoardGeese": 
                                                        System.out.println("Geese move valid, sending a request to update GUI!");
                                                        printBoardMatrix();
                                                        clnt.pw.println("UpdateBoardGeese:"+this.currentGeeseRow+":" + this.currentGeeseCol +":"+newRow+":"+newCol);
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        System.out.println("Invalid geese move!");
                                        this.pw.println("IllegalGeeseMove");
                                    }
                                    
                                    this.geeseState = "GEESE_SELECT";
                                    
                                    // Move handling logic
                                    this.myTurn = !this.myTurn;
                                    for(ConnectedClient clnt : this.allClients)
                                    {
                                        if(clnt.username.equals(this.foxPlayer))
                                        {
                                            clnt.myTurn = true;
                                            break;
                                        }
                                    }
                                    break;                                   
                            }
                        }                                             
                    }
                    else
                    {
                        System.out.println("Not my turn!");
                        this.pw.println("NotYourTurn");
                    }
                }
                
                /// NEW TYPE OF MESSAGE HERE ///
        }

    }
}