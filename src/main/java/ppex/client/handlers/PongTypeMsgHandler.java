package ppex.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMessageHandler;

import java.net.InetSocketAddress;

public class PongTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(PongTypeMsgHandler.class);

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) {
        LOGGER.info("client recv pong msg" + typeMessage.getBody());
    }
}
