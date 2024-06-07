package com.example.fox_geese;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class RecievedMessageFromServerGame implements Runnable {

    GameActivity parent;
    BufferedReader br;
    HashMap<String, ImageView> gameBoard;
    //private volatile boolean running = true;

    public RecievedMessageFromServerGame(GameActivity parent) {
        this.parent = parent;
        this.br = parent.getBr();
        this.gameBoard = parent.gameBoard;
    }

    @Override
    public void run() {
        while (true) {
            String line;
            try {
                line = this.br.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (line.startsWith("UpdateBoardFox"))
            {
                    String[] updateTokens = line.split(":");
                    String oldRow = updateTokens[1];
                    String oldCol = updateTokens[2];
                    String newRow = updateTokens[3];
                    String newCol = updateTokens[4];
                    String turnInfo = "Geese turn!";

                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageView oldImageView = parent.gameBoard.get(oldRow + "," + oldCol);
                            ImageView newImageView = parent.gameBoard.get(newRow + "," + newCol);

                            if (oldImageView != null && newImageView != null)
                            {
                                oldImageView.setImageResource(R.drawable.darksquare);
                                newImageView.setImageResource(R.drawable.darksquarefox);
                            }

                            parent.getTvTurn().setText(turnInfo);

                        }
                    });
                }

                if (line.startsWith("InvalidFoxMove"))
                {
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(parent, "Invalid fox move!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if (line.startsWith("GeeseSelectedOk"))
                {
                    System.out.println("I received a selected geese request");
                    String[] geeseToken = line.split(":");
                    String geeseRow = geeseToken[1];
                    String geeseCol = geeseToken[2];

                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(parent, "Geese with position: " + geeseRow + ":" + geeseCol + " selected!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if (line.startsWith("DidntSelectedGeese"))
                {
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(parent, "You did not select a geese!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if (line.startsWith("UpdateBoardGeese"))
                {
                    String[] updateTokens = line.split(":");
                    String oldRow = updateTokens[1];
                    String oldCol = updateTokens[2];
                    String newRow = updateTokens[3];
                    String newCol = updateTokens[4];
                    String turnInfo = "Fox turn!";

                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageView oldImageView = parent.gameBoard.get(oldRow + "," + oldCol);
                            ImageView newImageView = parent.gameBoard.get(newRow + "," + newCol);

                            if (oldImageView != null && newImageView != null)
                            {
                                oldImageView.setImageResource(R.drawable.darksquare);
                                newImageView.setImageResource(R.drawable.darksquaregeese);
                            }

                            parent.getTvTurn().setText(turnInfo);
                        }
                    });
                }

                if (line.startsWith("IllegalGeeseMove"))
                {
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(parent, "Illegal geese move!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if (line.startsWith("NotYourTurn"))
                {
                    String info = "Not your turn!";
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(parent, "Not your turn!", Toast.LENGTH_SHORT).show();
                            parent.getTvTurn().setText(info);
                        }
                    });
                }

                if (line.startsWith("GeeseWin")) {
                    String[] updateTokens = line.split(":");
                    String oldRow = updateTokens[1];
                    String oldCol = updateTokens[2];
                    String newRow = updateTokens[3];
                    String newCol = updateTokens[4];

                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageView oldImageView = parent.gameBoard.get(oldRow + "," + oldCol);
                            ImageView newImageView = parent.gameBoard.get(newRow + "," + newCol);

                            if (oldImageView != null && newImageView != null) {
                                oldImageView.setImageResource(R.drawable.darksquare);
                                newImageView.setImageResource(R.drawable.darksquaregeese);
                            }

                            new AlertDialog.Builder(parent)
                                    .setTitle("Geese Win")
                                    .setMessage("The geese have won! Do you want to play again?")
                                    .setPositiveButton("Yes", (dialog, which) -> parent.sendMessage("RestartGame"))
                                    .setNegativeButton("No", (dialog, which) -> parent.finish()) // Close the activity
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
                }

                if (line.startsWith("FoxWin")) {
                    String[] updateTokens = line.split(":");
                    String oldRow = updateTokens[1];
                    String oldCol = updateTokens[2];
                    String newRow = updateTokens[3];
                    String newCol = updateTokens[4];

                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageView oldImageView = parent.gameBoard.get(oldRow + "," + oldCol);
                            ImageView newImageView = parent.gameBoard.get(newRow + "," + newCol);

                            if (oldImageView != null && newImageView != null) {
                                oldImageView.setImageResource(R.drawable.darksquare);
                                newImageView.setImageResource(R.drawable.darksquarefox);
                            }

                            new AlertDialog.Builder(parent)
                                    .setTitle("Fox Win")
                                    .setMessage("The fox has won! Do you want to play again?")
                                    .setPositiveButton("Yes", (dialog, which) -> parent.sendMessage("RestartGame"))
                                    .setNegativeButton("No", (dialog, which) -> parent.finish())
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });


                }

        }
    }
}
