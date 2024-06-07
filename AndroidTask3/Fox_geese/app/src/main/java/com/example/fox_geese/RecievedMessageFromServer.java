package com.example.fox_geese;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;

public class RecievedMessageFromServer implements Runnable {
    MainActivity parent;
    BufferedReader br;
    private boolean running = true;
    public RecievedMessageFromServer(MainActivity parent) {
        this.parent = parent;
        this.br = parent.getBr();
    }

    @Override
    public void run() {

        while (running) {
            String line;
            try {
                line = this.br.readLine();

                /*
                if(line.startsWith("ConnectOk"))
                {
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parent.getBtnConnect().setEnabled(false);
                            parent.getEtIP().setEnabled(false);
                            parent.getBtnEnterRoom().setEnabled(true);
                            parent.getEtUsername().setEnabled(true);
                        }
                    });
                }
                 */

                // Receive a message, parse the data and in runOnUiThread method modify necessary GUI changes
                if (line.startsWith("NewList")) {
                    String[] userTokens = line.split(":");
                    String allUsernames = userTokens[1];
                    String[] usernames = allUsernames.split(",");
                    System.out.println("I recieved new list from server:" + line);
                    // Fill the spinner and modify the component availability
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parent.getSpAllPlayers().setAdapter(null);
                            Spinner spinner = parent.getSpAllPlayers();
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(parent, android.R.layout.simple_spinner_item, usernames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                            parent.getBtnEnterRoom().setEnabled(false);
                            parent.getEtUsername().setEnabled(false);
                            parent.getSpAllPlayers().setEnabled(true);
                            parent.getBtnChallenge().setEnabled(true);
                        }
                    });
                }

                // User got challenged so preview the challenger and enable buttons
                if(line.startsWith("Recieved challenge"))
                {
                    String [] challengeToken = line.split(":");
                    String challengedUser = challengeToken[2];
                    String challenger = challengeToken[1];
                    String challengeMessage = "Challenge from:"+challenger;
                    parent.setChallenger(challenger);
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parent.getTvChallenge().setText(challengeMessage);
                            parent.getBtnAccept().setEnabled(true);
                            parent.getBtnDecline().setEnabled(true);
                        }
                    });
                }

                // IF block that notifies declined user
                if(line.startsWith("Declined user"))
                {
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(MainActivity.this, "Challenge declined!", Toast.LENGTH_LONG).show();  why is this not working?
                            parent.getTvChallenge().setText("Challenge declined, try again later!");
                            //parent.getBtnDecline().setEnabled(false);
                            //parent.getBtnAccept().setEnabled(false);
                        }
                    });

                }

                // Both clients accepted a game, new activity should be presented to both??
                if(line.startsWith("Game on"))
                {
                    this.running = false;
                    Intent intent = new Intent(parent,GameActivity.class);
                    parent.startActivity(intent);
                }
                /// NEW BLOCK STARTS HERE ///
                ///if(line.startsWith(""))///
                ///                       ///
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
