package com.example.fox_geese;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Singleton {
    private static Singleton single_instance = null;
    public static Socket socket;
    public static BufferedReader br;
    public static PrintWriter pw;
    public static String ip_address;
    public static void setIP(String ip_addres){
        ip_address = ip_addres;
    }
    private Singleton() throws IOException {
        socket = new Socket(ip_address, 6001);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    public static synchronized Singleton getInstance() throws IOException {
        if(single_instance == null) {
            System.out.println("Creating singleton");
            single_instance = new Singleton();
        }
        return single_instance;
    }

}
