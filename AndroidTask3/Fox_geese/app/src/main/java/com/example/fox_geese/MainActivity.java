package com.example.fox_geese;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.spec.ECField;

public class MainActivity extends AppCompatActivity {

    Button btnConnect;
    Button btnEnterRoom;
    Button btnAccept;
    Button btnDecline;
    Button btnChallenge;
    EditText etIP;
    EditText etUsername;
    Spinner spAllPlayers;
    TextView tvChallenge;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private String challenger;
    public String getChallenger()
    {
        return this.challenger;
    }
    public void setChallenger(String challenger)
    {
        this.challenger = challenger;
    }
    public BufferedReader getBr()
    {
        return this.br;
    }
    public Spinner getSpAllPlayers()
    {
        return this.spAllPlayers;
    }
    public EditText getEtUsername()
    {
        return this.etUsername;
    }
    public EditText getEtIP(){return this.etIP;}
    public Button getBtnConnect()
    {
        return this.btnConnect;
    }

    public Button getBtnEnterRoom()
    {
        return this.btnEnterRoom;
    }

    public Button getBtnAccept()
    {
        return this.btnAccept;
    }

    public Button getBtnDecline()
    {
        return this.btnDecline;
    }

    public Button getBtnChallenge()
    {
        return this.btnChallenge;
    }

    public TextView getTvChallenge()
    {
        return this.tvChallenge;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        btnConnect = (Button) findViewById(R.id.btnConnect);

        btnChallenge = (Button) findViewById(R.id.btnChallenge);
        btnChallenge.setEnabled(false);

        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnAccept.setEnabled(false);

        btnDecline = (Button) findViewById(R.id.btnDecline);
        btnDecline.setEnabled(false);

        btnEnterRoom = (Button) findViewById(R.id.btnEnterRoom);
        btnEnterRoom.setEnabled(false);

        etIP = (EditText) findViewById(R.id.etIP);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etUsername.setEnabled(false);

        spAllPlayers = (Spinner) findViewById(R.id.spAllPlayers);
        spAllPlayers.setEnabled(false);

        tvChallenge = (TextView) findViewById(R.id.tvChallenge);

        // Button that connects to a server
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToServer();
                MainActivity.this.btnEnterRoom.setEnabled(true);
                MainActivity.this.etUsername.setEnabled(true);
                MainActivity.this.btnConnect.setEnabled(false);
                MainActivity.this.etIP.setEnabled(false);
                //new Thread(new RecievedMessageFromServer(MainActivity.this)).start();
            }
        });

        // Button that enters a room
        btnEnterRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.this.etUsername.getText().toString().equals(""))
                {
                    String usernameMessage = "Name:";
                    String messageToSend = usernameMessage + MainActivity.this.etUsername.getText().toString();
                    sendMessage(messageToSend);

                    new Thread(new RecievedMessageFromServer(MainActivity.this)).start();
                }
            }
        });

        // Button that issues a challenge
        btnChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userToChallenge = MainActivity.this.spAllPlayers.getSelectedItem().toString();
                String challenger = MainActivity.this.etUsername.getText().toString().trim();
                sendMessage("Challenge:"+userToChallenge+":"+challenger);

                //new Thread(new RecievedMessageFromServer(MainActivity.this)).start();
            }
        });

        // Button that declines a challenge - Report to challenger that he has been declined
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String declineInfo = tvChallenge.getText().toString();
                String [] declineToken = declineInfo.split(":");
                String whoGotDeclined = declineToken[1];
                sendMessage("Declined challenge:"+ whoGotDeclined);
                btnAccept.setEnabled(false);
                btnDecline.setEnabled(false);

                //new Thread(new RecievedMessageFromServer(MainActivity.this)).start();
            }
        });

        // Button that accepts challenge - here we transfer to new activity, but how?
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contestantInfo = tvChallenge.getText().toString();
                String [] contestantTokens = contestantInfo.split(":");
                String challenger = contestantTokens[1];
                String challengedUser = etUsername.getText().toString();
                sendMessage("Accepted challenge - Game on:" + challenger + ":" + challengedUser);

                //Intent intent = new Intent(MainActivity.this,GameActivity.class);
                //startActivity(intent);
                //new Thread(new RecievedMessageFromServer(MainActivity.this)).start();
            }
        });


        // What is the point of this and is it necessary?
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

        public void connectToServer(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.this.socket == null){
                        try {
                            if(!MainActivity.this.etIP.getText().toString().equals("")) {
                                String ip_address = MainActivity.this.etIP.getText().toString();
                                Singleton.setIP(ip_address);
                                Singleton singleton = Singleton.getInstance();
                                MainActivity.this.socket = singleton.socket;
                                MainActivity.this.br = singleton.br;
                                MainActivity.this.pw = singleton.pw;
                                //MainActivity.this.socket = new Socket(ip_address, 6001);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                       //try {
                            //MainActivity.this.br = new BufferedReader(new InputStreamReader(MainActivity.this.socket.getInputStream()));
                            //MainActivity.this.pw = new PrintWriter(new OutputStreamWriter(MainActivity.this.socket.getOutputStream()), true);
                        //} catch (IOException e) {
                        //    e.printStackTrace();
                        //}
                    }
                }
            }).start();
    }

    public void sendMessage(String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.this.pw != null){
                    MainActivity.this.pw.println(message);
                }
            }
        }).start();
    }
}