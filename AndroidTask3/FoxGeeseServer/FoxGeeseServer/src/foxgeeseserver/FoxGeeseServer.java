package foxgeeseserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoxGeeseServer {

    private ServerSocket ssocket;
    private int port;
    private ArrayList<ConnectedClient> clients;

    public ServerSocket getSsocket() {
        return ssocket;
    }

    public void setSsocket(ServerSocket ssocket) {
        this.ssocket = ssocket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void acceptClients() {
        Socket client = null;
        Thread thr;
        while (true) {
            try {
                System.out.println("Waiting for new clients..");
                client = this.ssocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(FoxGeeseServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (client != null) {
                ConnectedClient clnt = new ConnectedClient(client, clients);
                clients.add(clnt);
                thr = new Thread(clnt);
                thr.start();
            } else {
                break;
            }
        }
    }

    public FoxGeeseServer(int port) {
        this.clients = new ArrayList<>();
        try {
            this.port = port;
            this.ssocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(FoxGeeseServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        FoxGeeseServer server = new FoxGeeseServer(6001);
        System.out.println("Server pokrenut, slusam na portu 6001");
        server.acceptClients();

    }

}
