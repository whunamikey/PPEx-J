package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import ppex.proto.type.ThroughTypeMsg;
import ppex.proto.type.TypeMessageHandler;
import ppex.server.myturn.Connection;
import ppex.server.myturn.ConnectionService;
import ppex.utils.MessageUtil;

public class ThroughTypeMsgHandler implements TypeMessageHandler {

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception{
        ThroughTypeMsg ttmsg = MessageUtil.packet2ThroughMsg(packet);
        if (ttmsg.getAction() == ThroughTypeMsg.ACTION.SAVE_INFO.ordinal()){
            handleSaveInfo(ttmsg);
        }else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.GET_INFO.ordinal()){
            handleGetInfo(ctx,ttmsg,packet);
        }else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.CONNECT.ordinal()){
            handleConnect(ctx,ttmsg,packet);
        }else{
            throw new Exception("Unkown through msg action:" + ttmsg.toString());
        }
    }

    private void handleSaveInfo(ThroughTypeMsg ttmsg){
        ThroughTypeMsg.SAVEINFO saveinfo = JSON.parseObject(ttmsg.getContent(), ThroughTypeMsg.SAVEINFO.class);
        Connection connection = new Connection(saveinfo);
        ConnectionService.getInstance().addConnection(connection);
    }

    private void handleGetInfo(ChannelHandlerContext ctx,ThroughTypeMsg ttmsg,DatagramPacket packet){
        ttmsg.setAction(ThroughTypeMsg.ACTION.RECV_INFO.ordinal());
        ttmsg.setContent(ConnectionService.getInstance().getAllConnectionId());
        ctx.writeAndFlush(MessageUtil.throughmsg2Packet(ttmsg,packet.sender()));
    }

    private void handleConnect(ChannelHandlerContext ctx,ThroughTypeMsg ttmsg,DatagramPacket packet){
        long id = Long.parseLong(ttmsg.getContent());
        if (ConnectionService.getInstance().hasConnection(id)){
            //todo 19-10-10.探测两边nattype类型,然后进行尝试

        }else{
            ttmsg.setAction(ThroughTypeMsg.ACTION.RECV_INFO.ordinal());
            ThroughTypeMsg.RECVINFO recvinfo = ttmsg.new RECVINFO(ThroughTypeMsg.RECVTYPE.RESPONSE.ordinal(),"Don't find id:" + id);
            ttmsg.setContent(JSON.toJSONString(recvinfo));
            ctx.writeAndFlush(MessageUtil.throughmsg2Packet(ttmsg,packet.sender()));
        }
    }

}
