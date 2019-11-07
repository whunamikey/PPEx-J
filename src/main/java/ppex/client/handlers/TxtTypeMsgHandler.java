package ppex.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;

public class TxtTypeMsgHandler implements TypeMessageHandler {

//    @Override
//    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) throws Exception {
//
//    }

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx,RudpPack rudpPack, IAddrManager addrManager, TypeMessage tmsg) {

    }
}
