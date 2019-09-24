package ppex;

import ppex.socket.udp.UdpClient;

public class Client {
    public static void main(String[] args){
        UdpClient client = new UdpClient();
        client.startClient();
    }
}
