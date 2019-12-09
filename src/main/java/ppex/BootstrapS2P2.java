package ppex;

import ppex.server.socket.Server;
import ppex.utils.Identity;

public class BootstrapS2P2 {
    public static void main(String[] args) throws Exception {
        startServer();
    }

    public static void startServer() throws Exception {
        Server server = Server.getInstance();
        Identity.INDENTITY = Identity.Type.SERVER2_PORT2.ordinal();
        server.startServer(Identity.Type.SERVER2_PORT2);
    }

}
