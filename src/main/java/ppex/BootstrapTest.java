package ppex;

import ppex.server.socket.Server;

public class BootstrapTest {
    public static void main(String[] args){
        Server server = Server.getInstance();
        server.startTestServer();
    }
}
