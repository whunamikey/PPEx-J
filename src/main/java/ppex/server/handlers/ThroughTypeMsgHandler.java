package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.entity.through.CONNECT;
import ppex.proto.entity.through.RECVINFO;
import ppex.proto.entity.through.SAVEINFO;
import ppex.proto.type.ThroughTypeMsg;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMessageHandler;
import ppex.server.myturn.Connection;
import ppex.server.myturn.ConnectionService;
import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;

public class ThroughTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(ThroughTypeMsgHandler.class);

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromaddress) throws Exception {
//        ThroughTypeMsg ttmsg = MessageUtil.packet2ThroughMsg(packet);
        LOGGER.info("server handle ThroughTypeMsg");
        ThroughTypeMsg ttmsg = JSON.parseObject(typeMessage.getBody(), ThroughTypeMsg.class);
        if (ttmsg.getAction() == ThroughTypeMsg.ACTION.SAVE_INFO.ordinal()) {
            handleSaveInfo(ctx, ttmsg, fromaddress);
        } else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.GET_INFO.ordinal()) {
            handleGetInfo(ctx, ttmsg, fromaddress);
        } else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.CONNECT.ordinal()) {
            handleConnect(ctx, ttmsg, fromaddress);
        } else {
            throw new Exception("Unkown through msg action:" + ttmsg.toString());
        }
    }

    private void handleSaveInfo(ChannelHandlerContext ctx, ThroughTypeMsg ttmsg, InetSocketAddress address) {
        LOGGER.info("server handle through msg saveinfo:" + ttmsg.toString());
        try {
            SAVEINFO saveinfo = JSON.parseObject(ttmsg.getContent(), SAVEINFO.class);
            Connection connection = new Connection(saveinfo);
            ttmsg.setAction(ThroughTypeMsg.ACTION.RECV_INFO.ordinal());
            RECVINFO recvinfo = null;
            if (ConnectionService.getInstance().addConnection(connection)) {
                recvinfo = new RECVINFO(ThroughTypeMsg.RECVTYPE.SAVE_INFO.ordinal(), "success");
            } else {
                recvinfo = new RECVINFO(ThroughTypeMsg.RECVTYPE.SAVE_INFO.ordinal(), "fail");
            }
            ttmsg.setContent(JSON.toJSONString(recvinfo));
            ctx.writeAndFlush(MessageUtil.throughmsg2Packet(ttmsg, address));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetInfo(ChannelHandlerContext ctx, ThroughTypeMsg ttmsg, InetSocketAddress address) {
        LOGGER.info("server handle through msg getinfo:" + ttmsg.toString());
        try {
            ttmsg.setAction(ThroughTypeMsg.ACTION.RECV_INFO.ordinal());
            RECVINFO recvinfo = new RECVINFO(ThroughTypeMsg.RECVTYPE.GET_INFO.ordinal(), ConnectionService.getInstance().getAllConnectionId());
            ttmsg.setContent(JSON.toJSONString(recvinfo));
            ctx.writeAndFlush(MessageUtil.throughmsg2Packet(ttmsg, address));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleConnect(ChannelHandlerContext ctx, ThroughTypeMsg ttmsg, InetSocketAddress address) {
        LOGGER.info("server handle through msg connect:" + ttmsg.toString());
        try {
//            long id = Long.parseLong(ttmsg.getContent());
            CONNECT connect = JSON.parseObject(ttmsg.getContent(),CONNECT.class);
            if (!ConnectionService.getInstance().hasConnection(connect.getFrom()) || !ConnectionService.getInstance().hasConnection(connect.getTo())){
                ttmsg.setAction(ThroughTypeMsg.ACTION.RECV_INFO.ordinal());
                RECVINFO recvinfo = new RECVINFO(ThroughTypeMsg.RECVTYPE.CONNECT.ordinal(),"fail");
                ttmsg.setContent(JSON.toJSONString(recvinfo));
                ctx.writeAndFlush(MessageUtil.throughmsg2Packet(ttmsg,address));
                return;
            }
            if (ConnectionService.getInstance().hasConnection(connect.getTo()) && ConnectionService.getInstance().hasConnection(connect.getFrom())) {
                //todo 19-10-10.检测两边nattype类型,然后进行尝试
                ConnectionService.getInstance().connectPeers(connect.getFrom(),connect.getTo());
            } else {
                ttmsg.setAction(ThroughTypeMsg.ACTION.RECV_INFO.ordinal());
                RECVINFO recvinfo = new RECVINFO(ThroughTypeMsg.RECVTYPE.CONNECT.ordinal(), "fail");
                ttmsg.setContent(JSON.toJSONString(recvinfo));
                ctx.writeAndFlush(MessageUtil.throughmsg2Packet(ttmsg, address));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
