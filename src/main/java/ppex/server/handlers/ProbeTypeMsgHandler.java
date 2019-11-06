package ppex.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.msg.type.ProbeTypeMsg;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;
import ppex.server.entity.Server;
import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;

public class ProbeTypeMsgHandler implements TypeMessageHandler {

    private Logger LOGGER = Logger.getLogger(ProbeTypeMsgHandler.class);

//    @Override
//    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage typeMessage, InetSocketAddress address) throws Exception {
//        if (typeMessage.getType() != TypeMessage.Type.MSG_TYPE_PROBE.ordinal())
//            return;
//        ProbeTypeMsg pmsg = JSON.parseObject(typeMessage.getBody(),ProbeTypeMsg.class);
//        pmsg.setFromInetSocketAddress(address);
//        if (pmsg.getType() == ProbeTypeMsg.Type.FROM_CLIENT.ordinal()) {
//            if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal()) {
//                throw new Exception("Wrong ProbeTypeMsg:" + pmsg.toString());
//            } else if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
//                handleServer1FromClientMsg(ctx, pmsg);
//            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
//                handleServer2Port1FromClientMsg(ctx, pmsg);
//            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()) {
//                handleServer2Port2FromClientMsg(ctx, pmsg);
//            } else {
//                throw new Exception("Unknown ProbeTypeMsg:" + pmsg.toString());
//            }
//        } else if (pmsg.getType() == ProbeTypeMsg.Type.FROM_SERVER1.ordinal()) {
//            if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
//                throw new Exception("Wrong ProbeTypeMsg:" + pmsg.toString());
//            } else if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal()) {
////                handleServer1FromClientMsg(ctx,pmsg);
//            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
//                handleServer2Port1FromServer1Msg(ctx, pmsg);
//            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()) {
//                handleServer2Port2FromServer1Msg(ctx, pmsg);
//            } else {
//                throw new Exception("Unknown ProbeTypeMsg:" + pmsg.toString());
//            }
//        } else if (pmsg.getType() == ProbeTypeMsg.Type.FROM_SERVER2_PORT1.ordinal()) {
//            if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
//                throw new Exception("Wrong ProbeTypeMsg:" + pmsg.toString());
//            } else if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal()) {
////                handleServer2Port1FromClientMsg(ctx,pmsg);
//            } else if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
//                handleServer1FromServer2Port1Msg(ctx, pmsg);
//            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()) {
//                handleServer2Port2FromServer2Port1Msg(ctx, pmsg);
//            } else {
//                throw new Exception("Unknown ProbeTypeMsg:" + pmsg.toString());
//            }
//        } else if (pmsg.getType() == ProbeTypeMsg.Type.FROM_SERVER2_PORT2.ordinal()) {
//            if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()) {
//                throw new Exception("Wrong ProbeTypeMsg:" + pmsg.toString());
//            } else if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal()) {
//            } else if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
//            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
//            } else {
//                throw new Exception("Unknown ProbeTypeMsg:" + pmsg.toString());
//            }
//        } else {
//            throw new Exception("Unknown ProbeTypeMsg:" + pmsg.toString());
//        }
//    }


    //server1处理消息
    private void handleServer1FromClientMsg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        LOGGER.info("s1 handle msg recv from client:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.ONE.ordinal()) {
            //向client发回去包,并且向server2:port1发包
            //19-10-8优化,向Server2:Port2发包
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER1.ordinal());
            msg.setRecordInetSocketAddress(msg.getFromInetSocketAddress());
            msg.setFromInetSocketAddress(Server.getInstance().getSERVER1());
            ctx.writeAndFlush(MessageUtil.probemsg2Packet(msg, msg.getRecordInetSocketAddress()));               //这里发回给Client
            ctx.writeAndFlush(MessageUtil.probemsg2Packet(msg, Server.getInstance().getSERVER2P2()));            //这里发给S2P2
        }
    }

    private void handleServer1FromServer2Port1Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        //目前没有从server2port1发送到server1的消息
    }

    private void handleServer1FromServer2Port2Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        //没有从server2port2发送到server1的消息
    }

    //Server2:Port1处理消息
    private void handleServer2Port1FromClientMsg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        LOGGER.info("s2p1 handle msg recv from client:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.TWO.ordinal()) {
            //向Client发回去包,向S2P2发送包
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER2_PORT1.ordinal());
            msg.setRecordInetSocketAddress(msg.getFromInetSocketAddress());
            msg.setFromInetSocketAddress(Server.getInstance().getSERVER2P1());
            ctx.writeAndFlush(MessageUtil.probemsg2Packet(msg, msg.getRecordInetSocketAddress()));           //这里发回给Client
            ctx.writeAndFlush(MessageUtil.probemsg2Packet(msg, Server.getInstance().getSERVER2P2()));        //这里发给S2P2
        }
    }

    private void handleServer2Port1FromServer1Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        //暂时不会有s1发送给s2p1
    }

    private void handleServer2Port1FromServer2Port2Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        //暂时没有s2p2发给s2p1
    }

    //Server2:Port2处理消息
    private void handleServer2Port2FromClientMsg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        //暂时没有client发给s2p2
    }

    private void handleServer2Port2FromServer1Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        //第一阶段从Server1:Port1发送到的数据
        LOGGER.info("s2p2 handle msg from server1:" + msg.toString());
        if (msg.getType() == ProbeTypeMsg.Step.ONE.ordinal()) {
            InetSocketAddress inetSocketAddress = msg.getRecordInetSocketAddress();
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER2_PORT2.ordinal());
            msg.setRecordInetSocketAddress(msg.getFromInetSocketAddress());
            msg.setFromInetSocketAddress(Server.getInstance().getSERVER2P2());
            ctx.writeAndFlush(MessageUtil.probemsg2Packet(msg, inetSocketAddress));
        }
    }

    private void handleServer2Port2FromServer2Port1Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        LOGGER.info("s2p2 handle msg from s2p1:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.TWO.ordinal()) {
            InetSocketAddress inetSocketAddress = msg.getRecordInetSocketAddress();
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER2_PORT2.ordinal());
            msg.setFromInetSocketAddress(Server.getInstance().getSERVER2P2());
            ctx.writeAndFlush(MessageUtil.probemsg2Packet(msg, inetSocketAddress));
        }
    }


    @Override
    public void handleTypeMessage(RudpPack rudpPack, IAddrManager addrManager, TypeMessage tmsg) {

    }
}
