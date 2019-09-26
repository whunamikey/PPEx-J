package ppex.server.entity;

import java.net.InetSocketAddress;

public class Server {
    private static Server instance = null;
    private Server(){}
    public static Server getInstance(){
        if (instance == null)
            return new Server();
        return instance;
    }

    public String local_address = null;
    public InetSocketAddress SERVER1;
    public InetSocketAddress SERVER2P1;
    public InetSocketAddress SERVER2P2;
}
