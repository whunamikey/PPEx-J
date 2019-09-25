package ppex.proto.type;

import io.netty.channel.ChannelHandlerContext;

public interface TypeMessageHandler {
    default void handleTypeMessage(ChannelHandlerContext ctx,TypeMessage msg) throws Exception{
        System.out.println("handleTypemsg:" + msg.toString());
    }
}
