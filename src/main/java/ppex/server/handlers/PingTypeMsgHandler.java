package ppex.server.handlers;

import org.apache.log4j.Logger;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.RudpPack;

public class PingTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(PingTypeMsgHandler.class);

//    @Override
//    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) {
////        PingTypeMsg pingTypeMsg = JSON.parseObject(typeMessage.getBody(),PingTypeMsg.class);
//        PongTypeMsg pongTypeMsg = new PongTypeMsg();
//        ctx.writeAndFlush(MessageUtil.pongMsg2Packet(pongTypeMsg, fromAddress));
//    }

    @Override
    public void handleTypeMessage(RudpPack rudpPack, TypeMessage tmsg) {

    }
}
