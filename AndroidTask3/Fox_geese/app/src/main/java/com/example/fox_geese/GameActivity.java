package com.example.fox_geese;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    int numRows = 8;
    int numColumns = 8;
    HashMap<String, ImageView> gameBoard;
    TextView tvTurn;
    public TextView getTvTurn()
    {
        return this.tvTurn;
    }
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private static final int [] foxPossibilities = {1,3,5,7};
    private static long fixedSeed = 1234;
    private int foxPosition;
    private Random random;
    public BufferedReader getBr()
    {
        return this.br;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectToServer();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        gameBoard = new HashMap<String,ImageView>();
        LinearLayout llmain = findViewById(R.id.llmain);
        tvTurn = (TextView) findViewById(R.id.tvTurn);
        Random random = new Random(fixedSeed);
        this.random = random;

        // Open a new thread for a listener class
        new Thread(new RecievedMessageFromServerGame(GameActivity.this)).start();

        // Randomise fox players position
        foxPosition = getRandonFoxPosition();

        // Create an empty board first
        for(int row = 0; row < numRows; row++)
        {
            LinearLayout llrow = new LinearLayout(this);
            llrow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
            llrow.setLayoutParams(rowLayoutParams);
            for(int col = 0; col < numColumns ; col++)
            {
                ImageView iv = new ImageView(this);
                iv.setTag(row + "," + col);
                gameBoard.put(row + "," + col,iv);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1); // Modify this for full map view
                layoutParams.weight = 1;
                iv.setLayoutParams(layoutParams);

                // On odd rows, dark fields are on odd columns and light on even
                if(row % 2 == 0)
                {
                    if(col % 2 == 0)
                    {
                        iv.setImageResource(R.drawable.darksquare);
                    }
                    else
                    {
                        iv.setImageResource(R.drawable.lightsquare);
                    }
                }
                // On even rows, dark fields are on odd columns
                else
                {
                    if(col % 2 != 0)
                    {
                        iv.setImageResource(R.drawable.darksquare);
                    }
                    else
                    {
                        iv.setImageResource(R.drawable.lightsquare);
                    }
                }

                // Set the geese on top row dark squares
                if (row == 0 && col % 2 == 0) {
                    iv.setImageResource(R.drawable.darksquaregeese);
                }

                // Set the fox image on the randomly selected dark square in the last row
                if (row == numRows-1 && col == foxPosition) {
                    iv.setImageResource(R.drawable.darksquarefox);
                }

                // Once you click on a field, send field coordinates to server
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMessage("NewMove:" + v.getTag().toString());
                    }
                });
                llrow.addView(iv);
            }
            llmain.addView(llrow);

        }

        // Initial message to server, just needs a fox position to create a matrix map
        sendMessage("FoxPosition:"+foxPosition);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Function that gets random index for a new fox position
    private int getRandonFoxPosition()
    {
        int randomIndex = this.random.nextInt(foxPossibilities.length);
        return foxPossibilities[randomIndex];
    }

    // Method that reinitializes a board - this is bad implementation but i had issues with the idea of simply launching new activity
    public void reinitGame()
    {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        gameBoard = new HashMap<String,ImageView>();
        LinearLayout llmain = findViewById(R.id.llmain);
        tvTurn = (TextView) findViewById(R.id.tvTurn);

        foxPosition = getRandonFoxPosition();

        // Create an empty board first
        for(int row = 0; row < numRows; row++)
        {
            LinearLayout llrow = new LinearLayout(this);
            llrow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
            llrow.setLayoutParams(rowLayoutParams);
            for(int col = 0; col < numColumns ; col++)
            {
                ImageView iv = new ImageView(this);
                iv.setTag(row + "," + col);
                gameBoard.put(row + "," + col,iv);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1); // Modify this for full map view
                layoutParams.weight = 1;
                iv.setLayoutParams(layoutParams);

                // On odd rows, dark fields are on odd columns and light on even
                if(row % 2 == 0)
                {
                    if(col % 2 == 0)
                    {
                        iv.setImageResource(R.drawable.darksquare);
                    }
                    else
                    {
                        iv.setImageResource(R.drawable.lightsquare);
                    }
                }
                // On even rows, dark fields are on odd columns
                else
                {
                    if(col % 2 != 0)
                    {
                        iv.setImageResource(R.drawable.darksquare);
                    }
                    else
                    {
                        iv.setImageResource(R.drawable.lightsquare);
                    }
                }

                // Set the geese on top row dark squares
                if (row == 0 && col % 2 == 0) {
                    iv.setImageResource(R.drawable.darksquaregeese);
                }

                // Set the fox image on the randomly selected dark square in the last row
                if (row == numRows-1 && col == foxPosition) {
                    iv.setImageResource(R.drawable.darksquarefox);
                }

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(GameActivity.this, "You pressed on board field" +v.getTag().toString(),Toast.LENGTH_SHORT).show();
                        sendMessage("NewMove:" + v.getTag().toString());
                    }
                });
                llrow.addView(iv);
            }
            llmain.addView(llrow);
        }
        sendMessage("FoxPosition:"+foxPosition);
    }

    public void connectToServer(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Singleton singleton = null;
                try {
                    singleton = Singleton.getInstance();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (singleton != null){
                    GameActivity.this.socket = singleton.socket;
                    GameActivity.this.br = singleton.br;
                    GameActivity.this.pw = singleton.pw;
                }
                else {
                    System.out.println("Problem with socket, pw and br!");
                }
            }
        }).start();
    }

    public void sendMessage(String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (GameActivity.this.pw != null){
                    GameActivity.this.pw.println(message);
                }
            }
        }).start();
    }
}