package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.msg.entity.Connection;
import ppex.proto.msg.type.ProbeTypeMsg;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.Output;
import ppex.proto.rudp.RudpPack;
import ppex.server.socket.Server;
import ppex.utils.Identity;
import ppex.utils.MessageUtil;
import ppex.utils.tpool.DisruptorExectorPool;
import ppex.utils.tpool.IMessageExecutor;

import java.net.InetSocketAddress;

public class ProbeTypeMsgHandler implements TypeMessageHandler {

    private Logger LOGGER = Logger.getLogger(ProbeTypeMsgHandler.class);

    //server1处理消息
    private void handleServer1FromClientMsg(ChannelHandlerContext ctx, RudpPack rudpPack, IAddrManager addrManager, ProbeTypeMsg msg) {
        LOGGER.info("s1 handle msg rcv from client:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.ONE.ordinal()) {
            //向client发回去包,并且向server2:port1发包
            //19-10-8优化,向Server2:Port2发包
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER1.ordinal());
            msg.setRecordInetSocketAddress(msg.getFromInetSocketAddress());
            msg.setFromInetSocketAddress(Server.getInstance().getSERVER1());
//            ctx.writeAndFlush(MessageUtil.probemsg2Packet(msg, msg.getRecordInetSocketAddress()));               //这里发回给Client
//            ctx.writeAndFlush(MessageUtil.probemsg2Packet(msg, Server.getInstance().getSERVER2P2()));            //这里发给S2P2
            rudpPack = addrManager.get(msg.getRecordInetSocketAddress());
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
            rudpPack = addrManager.get(Server.getInstance().getSERVER2P2());
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
        }
    }

    private void handleServer1FromServer2Port1Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        //目前没有从server2port1发送到server1的消息
    }

    private void handleServer1FromServer2Port2Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        //没有从server2port2发送到server1的消息
    }

    //Server2:Port1处理消息
    private void handleServer2Port1FromClientMsg(ChannelHandlerContext ctx, RudpPack rudpPack, IAddrManager addrManager, ProbeTypeMsg msg) {
        LOGGER.info("s2p1 handle msg recv from client:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.TWO.ordinal()) {
            //向Client发回去包,向S2P2发送包
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER2_PORT1.ordinal());
            msg.setRecordInetSocketAddress(msg.getFromInetSocketAddress());
            msg.setFromInetSocketAddress(Server.getInstance().getSERVER2P1());
//            ctx.writeAndFlush(MessageUtil.probemsg2Packet(msg, msg.getRecordInetSocketAddress()));           //这里发回给Client
//            ctx.writeAndFlush(MessageUtil.probemsg2Packet(msg, Server.getInstance().getSERVER2P2()));        //这里发给S2P2
            rudpPack = addrManager.get(msg.getRecordInetSocketAddress());
            if (rudpPack == null){
                DisruptorExectorPool disruptorExectorPool = new DisruptorExectorPool();
                disruptorExectorPool.createDisruptorProcessor("test");
                Output output = new ServerOutput();
                IMessageExecutor executor = disruptorExectorPool.getAutoDisruptorProcessor();
                Connection connection = new Connection("", msg.getRecordInetSocketAddress(), "", 0, ctx.channel());
                rudpPack = new RudpPack(output, connection, executor, null,ctx);
                addrManager.New(msg.getRecordInetSocketAddress(), rudpPack);
            }
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
            rudpPack = addrManager.get(Server.getInstance().getSERVER2P2());
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
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

    private void handleServer2Port2FromServer1Msg(ChannelHandlerContext ctx, RudpPack rudpPack, IAddrManager addrManager, ProbeTypeMsg msg) {
        //第一阶段从Server1:Port1发送到的数据
        LOGGER.info("s2p2 handle msg from server1:" + msg.toString());
        if (msg.getType() == ProbeTypeMsg.Step.ONE.ordinal()) {
            InetSocketAddress inetSocketAddress = msg.getRecordInetSocketAddress();
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER2_PORT2.ordinal());
            msg.setRecordInetSocketAddress(msg.getFromInetSocketAddress());
            msg.setFromInetSocketAddress(Server.getInstance().getSERVER2P2());
//            ctx.writeAndFlush(MessageUtil.probemsg2Packet(msg, inetSocketAddress));

            rudpPack = addrManager.get(inetSocketAddress);
            if (rudpPack == null){
                DisruptorExectorPool disruptorExectorPool = new DisruptorExectorPool();
                disruptorExectorPool.createDisruptorProcessor("test");
                IMessageExecutor executor = disruptorExectorPool.getAutoDisruptorProcessor();
                Connection connection = new Connection("", inetSocketAddress, "", 0, ctx.channel());
                Output output = new ServerOutput();
                rudpPack = new RudpPack(output, connection, executor, null,ctx);
                addrManager.New(inetSocketAddress, rudpPack);
            }
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
        }
    }

    private void handleServer2Port2FromServer2Port1Msg(ChannelHandlerContext ctx, RudpPack rudpPack, IAddrManager addrManager, ProbeTypeMsg msg) {
        LOGGER.info("s2p2 handle msg from s2p1:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.TWO.ordinal()) {
            InetSocketAddress inetSocketAddress = msg.getRecordInetSocketAddress();
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER2_PORT2.ordinal());
            msg.setFromInetSocketAddress(Server.getInstance().getSERVER2P2());
            rudpPack = addrManager.get(inetSocketAddress);
            if (rudpPack == null){
                DisruptorExectorPool disruptorExectorPool = new DisruptorExectorPool();
                disruptorExectorPool.createDisruptorProcessor("test");
                IMessageExecutor executor = disruptorExectorPool.getAutoDisruptorProcessor();
                Output output = new ServerOutput();
                Connection connection = new Connection("", inetSocketAddress, "", 0, ctx.channel());
                rudpPack = new RudpPack(output, connection, executor, null,ctx);
                addrManager.New(inetSocketAddress, rudpPack);
            }
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
        }
    }


    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, RudpPack rudpPack, IAddrManager addrManager, TypeMessage tmsg) {
        if (tmsg.getType() != TypeMessage.Type.MSG_TYPE_PROBE.ordinal())
            return;
        LOGGER.info("ProbeTypemsg handler:" + tmsg.getBody());
        ProbeTypeMsg pmsg = JSON.parseObject(tmsg.getBody(), ProbeTypeMsg.class);
        pmsg.setFromInetSocketAddress(rudpPack.getConnection().address);
        if (pmsg.getType() == ProbeTypeMsg.Type.FROM_CLIENT.ordinal()) {
            if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal()) {
//                throw new Exception("Wrong ProbeTypeMsg:" + pmsg.toString());
            } else if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
                handleServer1FromClientMsg(ctx, rudpPack, addrManager, pmsg);
            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
                handleServer2Port1FromClientMsg(ctx, rudpPack, addrManager, pmsg);
            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()) {
                handleServer2Port2FromClientMsg(ctx, pmsg);
            } else {
//                throw new Exception("Unknown ProbeTypeMsg:" + pmsg.toString());
            }
        } else if (pmsg.getType() == ProbeTypeMsg.Type.FROM_SERVER1.ordinal()) {
            if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
//                throw new Exception("Wrong ProbeTypeMsg:" + pmsg.toString());
            } else if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal()) {
//                handleClientFromServer1(ctx,pmsg);
            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
                handleServer2Port1FromServer1Msg(ctx, pmsg);
            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()) {
                handleServer2Port2FromServer1Msg(ctx, rudpPack, addrManager, pmsg);
            } else {
//                throw new Exception("Unknown ProbeTypeMsg:" + pmsg.toString());
            }
        } else if (pmsg.getType() == ProbeTypeMsg.Type.FROM_SERVER2_PORT1.ordinal()) {
            if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
//                throw new Exception("Wrong ProbeTypeMsg:" + pmsg.toString());
            } else if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal()) {
//                handleServer2Port1FromClientMsg(ctx,rudpPack,addrManager,pmsg);
//                handleCLientFromServer2Port1Msg();
            } else if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
                handleServer1FromServer2Port1Msg(ctx, pmsg);
            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()) {
                handleServer2Port2FromServer2Port1Msg(ctx, rudpPack, addrManager, pmsg);
            } else {
//                throw new Exception("Unknown ProbeTypeMsg:" + pmsg.toString());
            }
        } else if (pmsg.getType() == ProbeTypeMsg.Type.FROM_SERVER2_PORT2.ordinal()) {
            if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()) {
//                throw new Exception("Wrong ProbeTypeMsg:" + pmsg.toString());
            } else if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal()) {
            } else if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
            } else {
//                throw new Exception("Unknown ProbeTypeMsg:" + pmsg.toString());
            }
        } else {
//            throw new Exception("Unknown ProbeTypeMsg:" + pmsg.toString());
        }
    }
}
