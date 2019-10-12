package ppex.utils;

public class Constants {
    public static byte MSG_VERSION = 1;

    public static int SEND_BUFSIZE=1024;
    public static int RECV_BUFSIZE=1024;

    public static String SERVER_LOCAL_IP = "127.0.0.1";
    public static String SERVER_LOCAL = "localhost";
    public static String SERVER_HOST1 = "119.139.199.127";
    public static String SERVER_HOST2 = "183.15.178.162";
//    public static String SERVER_HOST1 = "10.5.11.162";
//    public static String SERVER_HOST2 = "10.5.11.55";

    public static int PORT1 = 9123;
    public static int PORT2 = 9124;
    public static int PORT3 = 9125;

    public enum NATTYPE{
        UNKNOWN,
        SYMMETIC_NAT,
        PORT_RESTRICT_CONE_NAT,
        RESTRICT_CONE_NAT,
        FULL_CONE_NAT,
        PUBLIC_NETWORK
    }
}
