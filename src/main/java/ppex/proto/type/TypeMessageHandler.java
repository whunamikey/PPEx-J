package ppex.proto.type;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

public interface TypeMessageHandler {
    default void handleTypeMessage(ChannelHandlerContext ctx,DatagramPacket packet) throws Exception{
//        System.out.println("handleTypemsg:" + msg.toString());
    }
}
