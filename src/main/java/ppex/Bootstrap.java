package ppex;

import ppex.server.socket.Server;
import ppex.server.socket.UdpServer;
import ppex.utils.Identity;
import ppex.utils.LongIDUtil;

public class Bootstrap {

    public static void main(String[] args) throws Exception {
        startServer();
    }

    public static void startServer() throws Exception {
        Server server = Server.getInstance();
        Identity.INDENTITY = Identity.Type.SERVER1.ordinal();
        server.startServer(Identity.Type.SERVER1);
    }


}
