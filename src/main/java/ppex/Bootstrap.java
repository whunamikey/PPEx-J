package ppex;

import ppex.server.socket.UdpServer;

public class Bootstrap {

    public static void main(String[] args) {
        System.out.println("haha");
        startServer();
    }

    public static void startServer() {
        UdpServer server = new UdpServer();
        server.startUdpServer();
    }

}
