package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import ppex.proto.entity.Connection;
import ppex.proto.msg.type.ProbeTypeMsg;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.IOutput;
import ppex.proto.rudp.RudpPack;
import ppex.proto.rudp.RudpScheduleTask;
import ppex.server.rudp.ServerOutput;
import ppex.server.socket.Server;
import ppex.utils.Identity;
import ppex.utils.MessageUtil;
import ppex.utils.NatTypeUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 探测消息
 * client端发到Server1,Server2P1
 * Server1发给Server2P2
 * Server2P1发给Server2P2
 */
public class ProbeTypeMsgHandler implements TypeMessageHandler {

    private Logger LOGGER = Logger.getLogger(ProbeTypeMsgHandler.class);
    private Lock processLock = new ReentrantLock();

    @Override
    public void handleTypeMessage(RudpPack rudpPack, IAddrManager addrManager, TypeMessage tmsg) {
        if (tmsg.getType() != TypeMessage.Type.MSG_TYPE_PROBE.ordinal())
            return;
        System.out.println("ProbeTypeMsg handle:" + tmsg.toString());
        ProbeTypeMsg pmsg = JSON.parseObject(tmsg.getBody(), ProbeTypeMsg.class);
        pmsg.setFromInetSocketAddress(rudpPack.getOutput().getConn().getAddress());
        if (pmsg.getType() == ProbeTypeMsg.Type.FROM_CLIENT.ordinal()) {
            if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
                handleServer1FromClientMsg(rudpPack, addrManager, pmsg);
            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
                handleServer2Port1FromClientMsg(rudpPack, addrManager, pmsg);
            } else {
            }
        } else if (pmsg.getType() == ProbeTypeMsg.Type.FROM_SERVER1.ordinal()) {
            if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()) {
                handleServer2Port2FromServer1Msg(rudpPack, addrManager, pmsg);
            } else {
            }
        } else if (pmsg.getType() == ProbeTypeMsg.Type.FROM_SERVER2_PORT1.ordinal()) {
            if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()) {
                handleServer2Port2FromServer2Port1Msg(rudpPack, addrManager, pmsg);
            } else {
            }
        } else {
        }
    }

    //server1处理消息
    private void handleServer1FromClientMsg(RudpPack rudpPack, IAddrManager addrManager, ProbeTypeMsg msg) {
        LOGGER.info("s1 handle msg rcv from client:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.ONE.ordinal()) {
            //向client发回去包,并且向server2:port1发包
            //19-10-8优化,向Server2:Port2发包
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER1.ordinal());
            msg.setRecordInetSocketAddress(msg.getFromInetSocketAddress());
            msg.setFromInetSocketAddress(Server.getInstance().getAddrServer1());
            rudpPack = addrManager.get(msg.getRecordInetSocketAddress());
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
            rudpPack = addrManager.get(Server.getInstance().getAddrServer2p2());
//            rudpPack.sendReset();
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
        }
    }

    //Server2:Port1处理消息
    private void handleServer2Port1FromClientMsg(RudpPack rudpPack, IAddrManager addrManager, ProbeTypeMsg msg) {
        LOGGER.info("s2p1 handle msg recv from client:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.TWO.ordinal()) {
            //向Client发回去包,向S2P2发送包
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER2_PORT1.ordinal());
            msg.setRecordInetSocketAddress(msg.getFromInetSocketAddress());
            msg.setFromInetSocketAddress(Server.getInstance().getAddrServer2p1());

            Channel channel = rudpPack.getOutput().getChannel();

            rudpPack = addrManager.get(msg.getRecordInetSocketAddress());
            if (rudpPack == null) {
                Connection connection = new Connection("unknown", msg.getRecordInetSocketAddress(), "unknown", NatTypeUtil.NatType.UNKNOWN.getValue());
                IOutput output = new ServerOutput(channel, connection);
                rudpPack = new RudpPack(output, Server.getInstance().getExecutor(), Server.getInstance().getResponseListener());
            }
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
            rudpPack = addrManager.get(Server.getInstance().getAddrServer2p2());
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
        }
    }

    private void handleServer2Port2FromServer1Msg(RudpPack rudpPack, IAddrManager addrManager, ProbeTypeMsg msg) {
        //第一阶段从Server1:Port1发送到的数据
        LOGGER.info("s2p2 handle msg from server1:" + msg.toString());
        processLock.lock();
        if (msg.getStep() == ProbeTypeMsg.Step.ONE.ordinal()) {
            InetSocketAddress inetSocketAddress = msg.getRecordInetSocketAddress();
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER2_PORT2.ordinal());
            msg.setRecordInetSocketAddress(msg.getFromInetSocketAddress());
            msg.setFromInetSocketAddress(Server.getInstance().getAddrServer2p2());

            Channel channel = rudpPack.getOutput().getChannel();
            rudpPack = addrManager.get(inetSocketAddress);
            if (rudpPack == null) {
                Connection connection = new Connection("unknown", inetSocketAddress, "unknown", NatTypeUtil.NatType.UNKNOWN.getValue());
                IOutput output = new ServerOutput(channel, connection);
                rudpPack = new RudpPack(output, Server.getInstance().getExecutor(), Server.getInstance().getResponseListener());
                addrManager.New(inetSocketAddress,rudpPack);
            }
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
            RudpScheduleTask scheduleTask = new RudpScheduleTask(Server.getInstance().getExecutor(),rudpPack,addrManager);
            Server.getInstance().getExecutor().executeTimerTask(scheduleTask,rudpPack.getInterval());
        }
        processLock.unlock();
    }

    private void handleServer2Port2FromServer2Port1Msg(RudpPack rudpPack, IAddrManager addrManager, ProbeTypeMsg msg) {
        LOGGER.info("s2p2 handle msg from s2p1:" + msg.toString());
        processLock.lock();
        if (msg.getStep() == ProbeTypeMsg.Step.TWO.ordinal()) {
            InetSocketAddress inetSocketAddress = msg.getRecordInetSocketAddress();
            msg.setType(ProbeTypeMsg.Type.FROM_SERVER2_PORT2.ordinal());
            msg.setFromInetSocketAddress(Server.getInstance().getAddrServer2p2());
            Channel channel = rudpPack.getOutput().getChannel();
            rudpPack = addrManager.get(inetSocketAddress);
            if (rudpPack == null) {
                Connection connection = new Connection("unknown", inetSocketAddress, "unknown", NatTypeUtil.NatType.UNKNOWN.getValue());
                IOutput output = new ServerOutput(channel, connection);
                rudpPack = new RudpPack(output, Server.getInstance().getExecutor(), Server.getInstance().getResponseListener());
                addrManager.New(inetSocketAddress,rudpPack);
            }
            rudpPack.write(MessageUtil.probemsg2Msg(msg));
            RudpScheduleTask scheduleTask = new RudpScheduleTask(Server.getInstance().getExecutor(),rudpPack,addrManager);
            Server.getInstance().getExecutor().executeTimerTask(scheduleTask,rudpPack.getInterval());
        }
        processLock.unlock();
    }


}
