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
        // Randomly select one of the dark squares on the last row (2, 4, 6, 8)
        //Random random = new Random();
        //int[] darkColumns = {2, 4, 6, 8};
        //int randomDarkColumn = darkColumns[random.nextInt(darkColumns.length)];

        new Thread(new RecievedMessageFromServerGame(GameActivity.this)).start();

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
                if (row == numRows-1 && col == 1) {
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

        sendMessage("FoxPosition:1");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void reinitGame()
    {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        gameBoard = new HashMap<String,ImageView>();
        LinearLayout llmain = findViewById(R.id.llmain);
        tvTurn = (TextView) findViewById(R.id.tvTurn);
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
                if (row == numRows-1 && col == 1) {
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
        sendMessage("FoxPosition:1");
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