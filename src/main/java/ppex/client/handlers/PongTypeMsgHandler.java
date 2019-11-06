package ppex.client.handlers;

import org.apache.log4j.Logger;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;

public class PongTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(PongTypeMsgHandler.class);

//    @Override
//    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) {
//    }

    @Override
    public void handleTypeMessage(RudpPack rudpPack, IAddrManager addrManager, TypeMessage tmsg) {

    }
}
