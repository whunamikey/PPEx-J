package ppex;

import ppex.socket.udp.UdpClient;
import ppex.socket.udp.UdpServer;

public class Bootstrap {

    public static void main(String[] args) {
        System.out.println("haha");
        startServer();
//        startClient();
//        String str = "this is string";
//        StringBuilder builder = new StringBuilder(str);
//        try {
//            byte[] byteUtf8 = str.getBytes("UTF-8");
//            byte[] byteother8  = str.getBytes(CharsetUtil.UTF_8);
//            StringBuilder sb = new StringBuilder();
//            sb.append(str.getBytes(CharsetUtil.UTF_8));
//            String newStr = sb.toString();
//            System.out.println("nomal length:" + str.length() + " utf8len:" + byteUtf8.length + " netty:" + byteother8.length + " newStr:" + newStr);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    public static void startServer() {
        UdpServer server = new UdpServer();
        server.startUdpServer();
    }

    public static void startClient() {
        UdpClient client = new UdpClient();
        client.startClient();
    }
}
