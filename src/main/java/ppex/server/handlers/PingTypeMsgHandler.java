package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.type.PingTypeMsg;
import ppex.proto.type.PongTypeMsg;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMessageHandler;
import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;

public class PingTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(PingTypeMsgHandler.class);

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) {
        LOGGER.info("server handle ping msg" + typeMessage.getBody());
        PingTypeMsg pingTypeMsg = JSON.parseObject(typeMessage.getBody(),PingTypeMsg.class);
        PongTypeMsg pongTypeMsg = new PongTypeMsg();
        pongTypeMsg.setContent(pingTypeMsg.getContent());
        ctx.writeAndFlush(MessageUtil.pongMsg2Packet(pongTypeMsg,fromAddress));
    }
}
