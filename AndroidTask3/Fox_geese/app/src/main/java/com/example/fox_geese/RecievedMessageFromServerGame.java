package com.example.fox_geese;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class RecievedMessageFromServerGame implements Runnable{

    GameActivity parent;
    BufferedReader br;
    HashMap<String, ImageView> gameBoard;
    private volatile boolean running = true;
    public RecievedMessageFromServerGame(GameActivity parent)
    {
        this.parent = parent;
        this.br = parent.getBr();
        this.gameBoard = parent.gameBoard;
    }

    public void stop(){
        running = false;
    }

    @Override
    public void run()
    {
        while(running)
        {
            String line;

            try {
                synchronized (br) {
                    line = this.br.readLine();
                }
                if (line == null) {
                    break;
                }
            } catch (SocketTimeoutException e) {
                // Handle timeout
                continue; // Skip this iteration and continue the loop
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            // IF block that modifies board according to a successful fox move
            if(line.startsWith("UpdateBoardFox"))
            {
                System.out.println("I recieved a message from server.");
                String[]updateTokens = line.split(":");
                String oldRow = updateTokens[1];
                String oldCol = updateTokens[2];
                String newRow = updateTokens[3];
                String newCol = updateTokens[4];

                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView darkField = new ImageView(parent);    // Is parent here okay?
                        darkField.setImageResource(R.drawable.darksquare);

                        ImageView foxToken = new ImageView(parent);
                        foxToken.setImageResource(R.drawable.darksquarefox);

                        ImageView oldImageView = parent.gameBoard.get(oldRow + "," + oldCol);
                        ImageView newImageView = parent.gameBoard.get(newRow + "," + newCol);

                        if (oldImageView != null && newImageView != null) {
                            oldImageView.setImageResource(R.drawable.darksquare);
                            newImageView.setImageResource(R.drawable.darksquarefox);
                        }
                    }
                });

            }

            // IF block that notifies user that selected move is invalid
            if(line.startsWith("InvalidFoxMove"))
            {
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(parent, "Invalid fox move!",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // IF block that notifies that user successfully select geese
            if(line.startsWith("GeeseSelectedOk"))
            {
                System.out.println("I recieved a selected geese request");
                String [] geeseToken = line.split(":");
                String geeseRow = geeseToken[1];
                String geeseCol = geeseToken[2];

                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(parent, "Geese with position : "+ geeseRow + ":" + geeseCol + " selected!",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // IF block that notifies that user did not select geese field
            if(line.startsWith("DidntSelectedGeese"))
            {
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(parent, "You did not select a geese!",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // IF block that modifies board according to a successful geese move
            if(line.startsWith("UpdateBoardGeese"))
            {
                System.out.println("I reveived a geese move request");
                String[]updateTokens = line.split(":");
                String oldRow = updateTokens[1];
                String oldCol = updateTokens[2];
                String newRow = updateTokens[3];
                String newCol = updateTokens[4];

                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView darkField = new ImageView(parent);    // Is parent here okay?
                        darkField.setImageResource(R.drawable.darksquare);

                        ImageView foxToken = new ImageView(parent);
                        foxToken.setImageResource(R.drawable.darksquaregeese);

                        ImageView oldImageView = parent.gameBoard.get(oldRow + "," + oldCol);
                        ImageView newImageView = parent.gameBoard.get(newRow + "," + newCol);

                        if (oldImageView != null && newImageView != null) {
                            oldImageView.setImageResource(R.drawable.darksquare);
                            newImageView.setImageResource(R.drawable.darksquaregeese);
                        }
                    }
                });
            }

            // IF block that notifies that user made an illegal geese move
            if(line.startsWith("IllegalGeeseMove"))
            {
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(parent, "Illegal geese move!",Toast.LENGTH_SHORT).show();
                    }
                });            }

            // IF block that notifies that it is not current users move
            if(line.startsWith("NotYourTurn"))
            {
                String info = "Not your turn!";
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(parent, "Not your turn!",Toast.LENGTH_SHORT).show();
                        parent.getTvTurn().setText(info);
                    }
                });
            }

            // IF block that handles the geese win situation
            if(line.startsWith("GeeseWin"))
            {
                String[]updateTokens = line.split(":");
                String oldRow = updateTokens[1];
                String oldCol = updateTokens[2];
                String newRow = updateTokens[3];
                String newCol = updateTokens[4];

                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView darkField = new ImageView(parent);    // Is parent here okay?
                        darkField.setImageResource(R.drawable.darksquare);

                        ImageView foxToken = new ImageView(parent);
                        foxToken.setImageResource(R.drawable.darksquaregeese);

                        ImageView oldImageView = parent.gameBoard.get(oldRow + "," + oldCol);
                        ImageView newImageView = parent.gameBoard.get(newRow + "," + newCol);

                        if (oldImageView != null && newImageView != null) {
                            oldImageView.setImageResource(R.drawable.darksquare);
                            newImageView.setImageResource(R.drawable.darksquaregeese);
                        }

                        // PopUp window for a new game option
                        new AlertDialog.Builder(parent)
                                .setTitle("Geese Win")
                                .setMessage("The geese have won! Do you want to play again?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        parent.sendMessage("RestartGame");
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        parent.finish(); // Close the activity
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
            }

            // IF block that handles the fox win situation
            if(line.startsWith("FoxWin"))
            {
                String[]updateTokens = line.split(":");
                String oldRow = updateTokens[1];
                String oldCol = updateTokens[2];
                String newRow = updateTokens[3];
                String newCol = updateTokens[4];

                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView darkField = new ImageView(parent);    // Is parent here okay?
                        darkField.setImageResource(R.drawable.darksquare);

                        ImageView foxToken = new ImageView(parent);
                        foxToken.setImageResource(R.drawable.darksquarefox);

                        ImageView oldImageView = parent.gameBoard.get(oldRow + "," + oldCol);
                        ImageView newImageView = parent.gameBoard.get(newRow + "," + newCol);

                        if (oldImageView != null && newImageView != null) {
                            oldImageView.setImageResource(R.drawable.darksquare);
                            newImageView.setImageResource(R.drawable.darksquarefox);
                        }

                        // PopUp window for a new game option
                        new AlertDialog.Builder(parent)
                                .setTitle("Fox Win")
                                .setMessage("The fox has won! Do you want to play again?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        parent.sendMessage("RestartGame");
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        parent.finish();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
            }
        }
    }
}
