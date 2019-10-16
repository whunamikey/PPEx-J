package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.entity.through.Connect;
import ppex.proto.entity.through.Connection;
import ppex.proto.entity.through.RecvInfo;
import ppex.proto.type.ThroughTypeMsg;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMessageHandler;
import ppex.server.myturn.ConnectionService;
import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class ThroughTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(ThroughTypeMsgHandler.class);

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress fromaddress) throws Exception {
//        ThroughTypeMsg ttmsg = MessageUtil.packet2ThroughMsg(packet);
        LOGGER.info("server handle ThroughTypeMsg");
        ThroughTypeMsg ttmsg = JSON.parseObject(typeMessage.getBody(), ThroughTypeMsg.class);
        if (ttmsg.getAction() == ThroughTypeMsg.ACTION.SAVE_CONNINFO.ordinal()) {
            handleSaveInfo(ctx, ttmsg, fromaddress);
        } else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.GET_CONNINFO.ordinal()) {
            handleGetInfo(ctx, ttmsg, fromaddress);
        } else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.CONNECT_CONN.ordinal()) {
            handleConnect(ctx, ttmsg, fromaddress);
        } else {
            throw new Exception("Unkown through msg action:" + ttmsg.toString());
        }
    }

    private void handleSaveInfo(ChannelHandlerContext ctx, ThroughTypeMsg ttmsg, InetSocketAddress address) {
        LOGGER.info("server handle through msg saveinfo:" + ttmsg.toString());
        try {
            Connection connection = JSON.parseObject(ttmsg.getContent(), Connection.class);
            ttmsg.setAction(ThroughTypeMsg.ACTION.RECV_INFO.ordinal());
            RecvInfo recvinfo = null;
            if (ConnectionService.getInstance().addConnection(connection)) {
                recvinfo = new RecvInfo(ThroughTypeMsg.RECVTYPE.SAVE_CONNINFO.ordinal(), "success");
            } else {
                recvinfo = new RecvInfo(ThroughTypeMsg.RECVTYPE.SAVE_CONNINFO.ordinal(), "fail");
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
            List<Connection> connections = ConnectionService.getInstance().getAllConnections();
            RecvInfo recvinfo = new RecvInfo(ThroughTypeMsg.RECVTYPE.GET_CONNINFO.ordinal(), JSON.toJSONString(connections));
            ttmsg.setContent(JSON.toJSONString(recvinfo));
            ctx.writeAndFlush(MessageUtil.throughmsg2Packet(ttmsg, address));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleConnect(ChannelHandlerContext ctx, ThroughTypeMsg ttmsg, InetSocketAddress address) {
        LOGGER.info("server handle through msg connect:" + ttmsg.toString());
        try {
            ttmsg.setAction(ThroughTypeMsg.ACTION.RECV_INFO.ordinal());
            Connect connect = JSON.parseObject(ttmsg.getContent(), Connect.class);
            RecvInfo recvInfo = new RecvInfo(ThroughTypeMsg.RECVTYPE.CONNECT_CONN.ordinal());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
