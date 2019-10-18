package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import ppex.proto.type.FileTypeMsg;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMessageHandler;
import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;

public class FileTypeMsgHandler implements TypeMessageHandler {

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) {
        FileTypeMsg fileTypeMsg = JSON.parseObject(typeMessage.getBody(),FileTypeMsg.class);
        ctx.writeAndFlush(MessageUtil.typemsg2Packet(typeMessage,fileTypeMsg.getTo()));
    }
}
