package ppex.proto.type;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

public interface TypeMessageHandler {
    default void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) throws Exception{
//        System.out.println("handleTypemsg:" + msg.toString());
    }
}
