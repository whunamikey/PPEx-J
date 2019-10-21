package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import ppex.proto.type.TxtTypeMsg;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMessageHandler;
import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;

public class TxtTypeMsgHandler implements TypeMessageHandler {

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) {
        TxtTypeMsg txtTypeMsg = JSON.parseObject(typeMessage.getBody(),TxtTypeMsg.class);
        ctx.writeAndFlush(MessageUtil.txtMsg2packet(txtTypeMsg,txtTypeMsg.getTo()));
    }
}
