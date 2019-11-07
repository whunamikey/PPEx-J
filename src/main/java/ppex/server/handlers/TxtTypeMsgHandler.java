package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.msg.Message;
import ppex.proto.msg.type.TxtTypeMsg;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.Rudp;
import ppex.proto.rudp.RudpPack;
import ppex.utils.MessageUtil;

public class TxtTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(TxtTypeMsgHandler.class);

//    @Override
//    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromAddress) {
//        LOGGER.info("TxtTypeMsgHandler handle txt msg:" + typeMessage.getBody());
//        TxtTypeMsg txtTypeMsg = JSON.parseObject(typeMessage.getBody(), TxtTypeMsg.class);
//        if (txtTypeMsg.isReq())
//            ctx.writeAndFlush(MessageUtil.txtMsg2packet(txtTypeMsg, txtTypeMsg.getTo()));
//        else
//            ctx.writeAndFlush(MessageUtil.txtMsg2packet(txtTypeMsg,txtTypeMsg.getFrom()));
//    }

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, RudpPack rudpPack, IAddrManager addrManager, TypeMessage tmsg) {
        LOGGER.info("TxtTypemsg handle:" + tmsg.getBody());
        TxtTypeMsg txtTypeMsg = JSON.parseObject(tmsg.getBody(),TxtTypeMsg.class);
        if (txtTypeMsg.isReq()){
            RudpPack torudppack = addrManager.get(txtTypeMsg.getTo());
            torudppack.write(MessageUtil.txtmsg2Msg(txtTypeMsg));
        }else{
            RudpPack fromrudppack = addrManager.get(txtTypeMsg.getFrom());
            fromrudppack.write(MessageUtil.txtmsg2Msg(txtTypeMsg));
        }
    }
}
