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
    private InetSocketAddress SERVER1=null;
    private InetSocketAddress SERVER2P1=null;
    private InetSocketAddress SERVER2P2=null;

    public InetSocketAddress getSERVER1() {
        return SERVER1;
    }

    public void setSERVER1(InetSocketAddress SERVER1) {
        this.SERVER1 = SERVER1;
    }

    public InetSocketAddress getSERVER2P1() {
        return SERVER2P1;
    }

    public void setSERVER2P1(InetSocketAddress SERVER2P1) {
        this.SERVER2P1 = SERVER2P1;
    }

    public InetSocketAddress getSERVER2P2() {
        return SERVER2P2;
    }

    public void setSERVER2P2(InetSocketAddress SERVER2P2) {
        this.SERVER2P2 = SERVER2P2;
    }
}
