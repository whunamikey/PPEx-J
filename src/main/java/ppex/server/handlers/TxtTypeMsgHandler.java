package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.msg.type.TxtTypeMsg;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;

public class TxtTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(TxtTypeMsgHandler.class);

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) {
        LOGGER.info("TxtTypeMsgHandler handle txt msg:" + typeMessage.getBody());
        TxtTypeMsg txtTypeMsg = JSON.parseObject(typeMessage.getBody(), TxtTypeMsg.class);
        if (txtTypeMsg.isReq())
            ctx.writeAndFlush(MessageUtil.txtMsg2packet(txtTypeMsg, txtTypeMsg.getTo()));
        else
            ctx.writeAndFlush(MessageUtil.txtMsg2packet(txtTypeMsg,txtTypeMsg.getFrom()));
    }
}
