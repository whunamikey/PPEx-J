package ppex.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.msg.type.PongTypeMsg;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;
import ppex.utils.MessageUtil;

public class PingTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(PingTypeMsgHandler.class);

//    @Override
//    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) {
////        PingTypeMsg pingTypeMsg = JSON.parseObject(typeMessage.getBody(),PingTypeMsg.class);
//        PongTypeMsg pongTypeMsg = new PongTypeMsg();
//        ctx.writeAndFlush(MessageUtil.pongMsg2Packet(pongTypeMsg, fromAddress));
//    }

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx,RudpPack rudpPack, IAddrManager addrManager, TypeMessage tmsg) {
        PongTypeMsg pongTypeMsg = new PongTypeMsg();
        rudpPack.write(MessageUtil.pongmsg2Msg(pongTypeMsg));
//        ctx.writeAndFlush(MessageUtil.pongMsg2Packet(pongTypeMsg, fromAddress));
    }
}
