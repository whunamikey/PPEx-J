package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import ppex.proto.entity.Connection;
import ppex.proto.entity.through.Connect;
import ppex.proto.entity.through.RecvInfo;
import ppex.proto.msg.type.ThroughTypeMsg;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;
import ppex.server.myturn.ConnectionService;
import ppex.utils.MessageUtil;

import java.util.List;

public class ThroughTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(ThroughTypeMsgHandler.class);

    @Override
    public void handleTypeMessage(RudpPack rudpPack, IAddrManager addrManager, TypeMessage tmsg) {

        ThroughTypeMsg ttmsg = JSON.parseObject(tmsg.getBody(), ThroughTypeMsg.class);
        if (ttmsg.getAction() == ThroughTypeMsg.ACTION.SAVE_CONNINFO.ordinal()) {
            handleSaveInfo(ttmsg,rudpPack);
        } else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.GET_CONNINFO.ordinal()) {
            handleGetInfo(ttmsg,rudpPack);
        } else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.CONNECT_CONN.ordinal()) {
            handleConnect(ttmsg,rudpPack,addrManager);
        } else {
//            throw new Exception("Unkown through msg action:" + ttmsg.toString());
        }
    }

    private void handleSaveInfo(ThroughTypeMsg ttmsg,RudpPack rudpPack) {
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
//            ctx.writeAndFlush(MessageUtil.throughmsg2Packet(ttmsg, address));
            rudpPack.write(MessageUtil.throughmsg2Msg(ttmsg));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetInfo(ThroughTypeMsg ttmsg, RudpPack rudpPack) {
        LOGGER.info("server handle through msg getinfo:" + ttmsg.toString());
        try {
            ttmsg.setAction(ThroughTypeMsg.ACTION.RECV_INFO.ordinal());
            List<Connection> connections = ConnectionService.getInstance().getAllConnections();
            RecvInfo recvinfo = new RecvInfo(ThroughTypeMsg.RECVTYPE.GET_CONNINFO.ordinal(), JSON.toJSONString(connections));
            ttmsg.setContent(JSON.toJSONString(recvinfo));
//            ctx.writeAndFlush(MessageUtil.throughmsg2Packet(ttmsg, address));
            rudpPack.write(MessageUtil.throughmsg2Msg(ttmsg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleConnect(ThroughTypeMsg ttmsg,RudpPack rudpPack,IAddrManager addrManager) {
        try {
            ttmsg.setAction(ThroughTypeMsg.ACTION.RECV_INFO.ordinal());
            Connect connect = JSON.parseObject(ttmsg.getContent(), Connect.class);
            RecvInfo recvInfo = new RecvInfo(ThroughTypeMsg.RECVTYPE.CONNECT_CONN.ordinal());
            //收到打洞消息
            List<Connection> connections = JSON.parseArray(connect.getContent(), Connection.class);
            if (connect.getType() == Connect.TYPE.HOLE_PUNCH.ordinal()) {
                LOGGER.info("server handle connect hole_punch msg :" + connect.toString());
                //转发消息给B
                recvInfo.recvinfos = JSON.toJSONString(connect);
                ttmsg.setContent(JSON.toJSONString(recvInfo));
                rudpPack = addrManager.get(connections.get(1).getAddress());
                rudpPack.write(MessageUtil.throughmsg2Msg(ttmsg));
            } else if (connect.getType() == Connect.TYPE.CONNECTING.ordinal()) {
                LOGGER.info("server handle connect connecting msg :" + connect.toString());
                ConnectionService.getInstance().addConnecting(connect.getType(), connections);
            } else if (connect.getType() == Connect.TYPE.CONNECTED.ordinal()) {
                LOGGER.info("server handle connect connected msg :" + connect.toString());
                ConnectionService.getInstance().addConnected(connect.getType(), connections);
            } else if (connect.getType() == Connect.TYPE.RETURN_HOLE_PUNCH.ordinal()) {
                //转回给A
                LOGGER.info("server handle connect return_hole_punch msg :" + connect.toString());
                recvInfo.recvinfos = JSON.toJSONString(connect);
                ttmsg.setContent(JSON.toJSONString(recvInfo));
                rudpPack = addrManager.get(connections.get(0).getAddress());
                rudpPack.write(MessageUtil.throughmsg2Msg(ttmsg));
            } else if (connect.getType() == Connect.TYPE.REVERSE.ordinal()) {
                LOGGER.info("server handle connect reverse msg :" + connect.toString());
                recvInfo.recvinfos = JSON.toJSONString(connect);
                ttmsg.setContent(JSON.toJSONString(recvInfo));
                rudpPack = addrManager.get(connections.get(1).getAddress());
                rudpPack.write(MessageUtil.throughmsg2Msg(ttmsg));
            } else if (connect.getType() == Connect.TYPE.FORWARD.ordinal()) {
                LOGGER.info("server handle connect forward msg :" + connect.toString());
                recvInfo.recvinfos = JSON.toJSONString(connect);
                ttmsg.setContent(JSON.toJSONString(recvInfo));
                rudpPack = addrManager.get(connections.get(1).getAddress());
                rudpPack.write(MessageUtil.throughmsg2Msg(ttmsg));
            } else if (connect.getType() == Connect.TYPE.RETURN_FORWARD.ordinal()) {
                LOGGER.info("server handle connect return_forward msg :" + connect.toString());
                recvInfo.recvinfos = JSON.toJSONString(connect);
                ttmsg.setContent(JSON.toJSONString(recvInfo));
                rudpPack = addrManager.get(connections.get(0).getAddress());
                rudpPack.write(MessageUtil.throughmsg2Msg(ttmsg));
            }else{
                throw new Exception("Unknow connect operate :" + connect.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
