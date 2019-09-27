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
        SYMMETIC_NAT,
        UNKNOWN,
        PORT_RESTRICT_CONE_NAT,
        RESTRICT_CONE_NAT,
        FULL_CONE_NAT,
        PUBLIC_NETWORK
    }

    public String local_address = null;
    public int NAT_TYPE = NATTYPE.UNKNOWN.ordinal();
    public InetSocketAddress SERVER1;
    public InetSocketAddress SERVER2P1;
    public InetSocketAddress SERVER2P2;
    public InetSocketAddress STEP_ONE_NAT_ADDRESS;

}
