package ppex.client.entity;

import ppex.utils.Constants;

import java.net.InetSocketAddress;

public class Client {

    private static Client instance =null;

    private Client() {
    }

    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public long id = 1;
    public String peerName = "1";
    public InetSocketAddress address;
    public int NAT_TYPE = Constants.NATTYPE.UNKNOWN.ordinal();

    public String local_address = null;
    public InetSocketAddress SERVER1;
    public InetSocketAddress SERVER2P1;
    public InetSocketAddress SERVER2P2;




}
