package ppex.client.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import ppex.proto.type.ThroughTypeMsg;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMessageHandler;
import ppex.server.myturn.Connection;
import ppex.server.myturn.ConnectionService;
import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;

public class ThroughTypeMsgHandler implements TypeMessageHandler {

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage msg, InetSocketAddress address) throws Exception{
//        ThroughTypeMsg ttmsg = MessageUtil.packet2ThroughMsg(packet);
        ThroughTypeMsg ttmsg = JSON.parseObject(msg.getBody(),ThroughTypeMsg.class);
        if (ttmsg.getAction() == ThroughTypeMsg.ACTION.SAVE_INFO.ordinal()){
        }else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.GET_INFO.ordinal()){
        }else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.CONNECT.ordinal()){
        }else{
            throw new Exception("Unkown through msg action:" + ttmsg.toString());
        }
    }


}
