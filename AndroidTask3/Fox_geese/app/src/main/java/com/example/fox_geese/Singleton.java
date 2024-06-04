package com.example.fox_geese;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Singleton {
    private static Singleton single_instance = null;
    public Socket socket;
    public BufferedReader br;
    public PrintWriter pw;
    public String ip_address;
    private Singleton(String ip_address) throws IOException {
        // What to do here with IP address?
        this.socket = new Socket(ip_address, 6001);
        this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.pw = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
    }

    public static synchronized Singleton getInstance(String ip_address) throws IOException {
        if(single_instance == null)
            single_instance = new Singleton(ip_address);
        return single_instance;
    }

}
