package ppex.client.entity;

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

    public enum NATTYPE{
        UNKNOWN,
        SYMMETIC_NAT,
        PORT_RESTRICT_CONE_NAT,
        RESTRICT_CONE_NAT,
        FULL_CONE_NAT,
        PUBLIC_NETWORK
    }

    public long id = 1;
    public String peerName = "1";
    public InetSocketAddress address;
    public int NAT_TYPE = NATTYPE.UNKNOWN.ordinal();

    public String local_address = null;
    public InetSocketAddress SERVER1;
    public InetSocketAddress SERVER2P1;
    public InetSocketAddress SERVER2P2;




}
