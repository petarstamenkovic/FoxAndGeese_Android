package com.example.fox_geese;

import java.io.BufferedReader;
import java.io.IOException;

public class RecievedMessageFromServerGame implements Runnable{

    GameActivity parent;
    BufferedReader br;

    public RecievedMessageFromServerGame(GameActivity parent)
    {
        this.parent = parent;
        this.br = parent.getBr();
    }

    @Override
    public void run()
    {
        while(true)
        {
            String line;

            try {
                line = this.br.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // IF block that modifies board according to a successful fox move
            if(line.startsWith("UpdateBoardFox"))
            {

            }

            // IF block that notifies user that selected move is invalid
            if(line.startsWith("InavlidFoxMove"))
            {

            }

            // IF block that notifies that user successfully select geese
            if(line.startsWith("GeeseSelectedOk"))
            {

            }

            // IF block that notifies that user did not select geese field
            if(line.startsWith("DidntSelectedGeese"))
            {

            }

            // IF block that modifies board according to a successful geese move
            if(line.startsWith("UpdateBoardGeese"))
            {

            }

            // IF block that notifies that user made an illegal geese move
            if(line.startsWith("IllegalGeeseMove"))
            {

            }

            // IF block that notifies that it is not current users move
            if(line.startsWith("NotYourTurn"))
            {

            }
        }
    }
}
