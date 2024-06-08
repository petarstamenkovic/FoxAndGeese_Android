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
    private boolean running;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    public RecievedMessageFromServer(MainActivity parent) {
        this.parent = parent;
        this.br = parent.getBr();
        this.running = true;
    }


    // Receive a message, parse the data and in runOnUiThread method modify necessary GUI changes
    @Override
    public void run() {

        while (running) {
            String line;
            try {
                line = this.br.readLine();

                // Update spinner with new players and modify GUI
                if (line.startsWith("NewList")) {
                    String[] userTokens = line.split(":");
                    String allUsernames = userTokens[1];
                    String[] usernames = allUsernames.split(",");

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
                            parent.getTvChallenge().setText("Challenge declined, try again later!");
                        }
                    });

                }

                // Both clients accepted a game - launch a new activity
                if(line.startsWith("Game on"))
                {
                    this.running = false;
                    Intent intent = new Intent(parent,GameActivity.class);
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parent.btnAccept.setEnabled(false);
                            parent.btnDecline.setEnabled(false);
                            parent.activity2Launcher.launch(intent);
                        }
                    });

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
