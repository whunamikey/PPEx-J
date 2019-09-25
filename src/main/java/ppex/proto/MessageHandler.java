package ppex.proto;

import io.netty.channel.socket.DatagramPacket;

public interface MessageHandler {

    default void handleDatagramPacket(DatagramPacket packet){
        System.out.println("handle pack:" + packet.toString());
    }
}
