import ppex.socket.udp.UdpClient;
import ppex.socket.udp.UdpServer;

public class Bootstrap {

    public static void main(String[] args){
        System.out.println("haha");
//        startServer();
        startClient();
    }

    public static void startServer(){
        UdpServer server = new UdpServer();
        server.startUdpServer();
    }
    public static void startClient(){
        UdpClient client = new UdpClient();
        client.startClient();
    }
}
