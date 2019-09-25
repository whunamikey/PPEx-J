package ppex.client.entity;

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
        FULL_CONE_NAT
    }

    public String local_address = null;
    public int NAT_TYPE = NATTYPE.UNKNOWN.ordinal();
    public String NAT_FROM_SERVER1 = null;
    public String NAT_FROM_SERVER2 = null;

}
