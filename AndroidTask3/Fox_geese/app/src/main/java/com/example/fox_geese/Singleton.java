package com.example.fox_geese;

import java.net.Socket;

public class Singleton {
    private static Singleton single_instance = null;
    Socket socket;

    // Here you have to create a socket?
    private Singleton()
    {

    }

    public static synchronized Singleton getInstance()
    {
        if(single_instance == null)
            single_instance = new Singleton();

        return single_instance;
    }

}
