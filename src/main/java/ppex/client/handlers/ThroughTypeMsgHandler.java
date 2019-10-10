package ppex.client.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.client.process.ThroughProcess;
import ppex.proto.entity.through.RECVINFO;
import ppex.proto.type.ThroughTypeMsg;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMessageHandler;

import java.net.InetSocketAddress;

public class ThroughTypeMsgHandler implements TypeMessageHandler {
    private static Logger LOGGER = Logger.getLogger(ThroughTypeMsgHandler.class);

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage msg, InetSocketAddress address) throws Exception {
//        ThroughTypeMsg ttmsg = MessageUtil.packet2ThroughMsg(packet);
        ThroughTypeMsg ttmsg = JSON.parseObject(msg.getBody(), ThroughTypeMsg.class);
        if (ttmsg.getAction() != ThroughTypeMsg.ACTION.RECV_INFO.ordinal())
            return;
        RECVINFO recvinfo = JSON.parseObject(ttmsg.getContent(), RECVINFO.class);
        if (ttmsg.getAction() == ThroughTypeMsg.ACTION.SAVE_INFO.ordinal()) {
            handleSaveInfoFromServer(recvinfo);
        } else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.GET_INFO.ordinal()) {
            handleGetInfoFromServer(recvinfo);
        } else if (ttmsg.getAction() == ThroughTypeMsg.ACTION.CONNECT.ordinal()) {
            handleConnectFromServer(recvinfo);
        } else {
            throw new Exception("Unkown through msg action:" + ttmsg.toString());
        }
    }

    private void handleSaveInfoFromServer(RECVINFO recvinfo) {
        if (recvinfo.type != ThroughTypeMsg.RECVTYPE.SAVE_INFO.ordinal()) {
            return;
        }
        if (!recvinfo.recvinfos.equals("success")) {
            ThroughProcess.getInstance().sendSaveInfo();
        } else {
            //todo 做心跳连接
            ThroughProcess.getInstance().getIDSInfoFromServer();
        }
    }

    private void handleGetInfoFromServer(RECVINFO recvinfo) {
        if (recvinfo.type != ThroughTypeMsg.RECVTYPE.GET_INFO.ordinal())
            return;
        System.out.println("all ids:" + recvinfo.recvinfos);
        //todo 获取ids之后就开始连接
        ThroughProcess.getInstance().connectOthrePeer(2);
    }

    private void handleConnectFromServer(RECVINFO recvinfo) {
        if (recvinfo.type != ThroughTypeMsg.RECVTYPE.CONNECT.ordinal())
            return;
        if (recvinfo.recvinfos.equals("fail")) {
            LOGGER.info("connect fail");
        } else {
            //todo 服务端还没实现nattype类型尝试
        }
    }

}
