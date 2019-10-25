package ppex.client.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.client.process.ThroughProcess;
import ppex.proto.msg.entity.through.Connect;
import ppex.proto.msg.entity.through.Connection;
import ppex.proto.msg.entity.through.RecvInfo;
import ppex.proto.msg.type.ThroughTypeMsg;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;

import java.net.InetSocketAddress;
import java.util.List;

public class ThroughTypeMsgHandler implements TypeMessageHandler {
    private static Logger LOGGER = Logger.getLogger(ThroughTypeMsgHandler.class);

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage msg, InetSocketAddress address) throws Exception {
//        ThroughTypeMsg ttmsg = MessageUtil.packet2ThroughMsg(packet);
        ThroughTypeMsg ttmsg = JSON.parseObject(msg.getBody(), ThroughTypeMsg.class);
        if (ttmsg.getAction() == ThroughTypeMsg.ACTION.RECV_INFO.ordinal()) {
            RecvInfo recvinfo = JSON.parseObject(ttmsg.getContent(), RecvInfo.class);
            if (recvinfo.type == ThroughTypeMsg.RECVTYPE.SAVE_CONNINFO.ordinal()) {
                handleSaveInfoFromServer(ctx, recvinfo);
            } else if (recvinfo.type == ThroughTypeMsg.RECVTYPE.GET_CONNINFO.ordinal()) {
                handleGetInfoFromServer(ctx, recvinfo);
            } else if (recvinfo.type == ThroughTypeMsg.RECVTYPE.CONNECT_CONN.ordinal()) {
                handleConnectFromServer(ctx, recvinfo);
            } else {
                throw new Exception("Unkown through msg action:" + ttmsg.toString());
            }
        }else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.CONNECT_CONN.ordinal()){
//            handleConnecCONN(ctx,ttmsg,address);
        }
    }

    private void handleSaveInfoFromServer(ChannelHandlerContext ctx, RecvInfo recvinfo) {
        LOGGER.info("client handle ThroughTypeMsg saveinfo from server:" + recvinfo.toString());
        if (recvinfo.type != ThroughTypeMsg.RECVTYPE.SAVE_CONNINFO.ordinal()) {
            return;
        }
        if (!recvinfo.recvinfos.equals("success")) {
            ThroughProcess.getInstance().sendSaveInfo();
        } else {
            //todo 增加心跳连接
            LOGGER.info("client get ids from server");
            ThroughProcess.getInstance().getConnectionsFromServer(ctx);
        }
    }

    private void handleGetInfoFromServer(ChannelHandlerContext ctx, RecvInfo recvinfo) {
        LOGGER.info("client handle ThroughTypeMsg getInfo from server:" + recvinfo.toString());
        if (recvinfo.type != ThroughTypeMsg.RECVTYPE.GET_CONNINFO.ordinal())
            return;
        LOGGER.info("all connection:" + recvinfo.recvinfos);
        List<Connection> connections = JSON.parseArray(recvinfo.recvinfos, Connection.class);
        //todo 发送要连接的Connection信息
        ThroughProcess.getInstance().connectOtherPeer(ctx, connections.get(0));
    }

    private void handleConnectFromServer(ChannelHandlerContext ctx, RecvInfo recvinfo) {
        LOGGER.info("client handle ThroughTypeMsg connect from server:" + recvinfo.toString());
        if (recvinfo.type != ThroughTypeMsg.RECVTYPE.CONNECT_CONN.ordinal())
            return;
        Connect connect = JSON.parseObject(recvinfo.recvinfos, Connect.class);


    }


}
