package ppex.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;

public class FileTypeMsgHandler implements TypeMessageHandler {

//    @Override
//    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) {
//        FileTypeMsg fileTypeMsg = JSON.parseObject(typeMessage.getBody(),FileTypeMsg.class);
//        ctx.writeAndFlush(MessageUtil.typemsg2Packet(typeMessage,fileTypeMsg.getTo()));
//    }

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, RudpPack rudpPack, IAddrManager addrManager, TypeMessage tmsg) {

    }
}
