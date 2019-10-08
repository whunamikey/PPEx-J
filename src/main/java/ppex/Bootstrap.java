package ppex;

import ppex.server.socket.UdpServer;
import ppex.utils.Identity;

public class Bootstrap {

    public static void main(String[] args) {
        System.out.println("haha");
//        startServer();
    }

    public static void startServer() {
        //修改Identity已匹配服务类型
        Identity.INDENTITY = Identity.Type.SERVER1.ordinal();
//        Identity.INDENTITY = Identity.Type.SERVER2_PORT1.ordinal();
//        Identity.INDENTITY = Identity.Type.SERVER2_PORT2.ordinal();

        UdpServer server = new UdpServer();
        server.startUdpServer(Identity.INDENTITY);
    }



}
