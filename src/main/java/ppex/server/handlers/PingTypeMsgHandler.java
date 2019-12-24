package ppex.server.handlers;

import org.apache.log4j.Logger;
import ppex.proto.msg.type.PongTypeMsg;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;
import ppex.utils.MessageUtil;

public class PingTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(PingTypeMsgHandler.class);

    @Override
    public void handleTypeMessage(RudpPack rudpPack, IAddrManager addrManager, TypeMessage tmsg) {
        PongTypeMsg pongTypeMsg = new PongTypeMsg();
        rudpPack.send2(MessageUtil.pongmsg2Msg(pongTypeMsg));
//        ctx.writeAndFlush(MessageUtil.pongMsg2Packet(pongTypeMsg, fromAddress));
    }
}
