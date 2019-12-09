package ppex;

import ppex.server.socket.Server;
import ppex.server.socket.UdpServer;
import ppex.utils.Identity;

public class BootstrapS2P1 {
    public static void main(String[] args) throws Exception {
        startServer();
    }

    public static void startServer() throws Exception {
        Server server = Server.getInstance();
        Identity.INDENTITY = Identity.Type.SERVER2_PORT1.ordinal();
        server.startServer(Identity.Type.SERVER2_PORT1);
    }

}
