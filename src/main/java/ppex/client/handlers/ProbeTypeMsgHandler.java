package ppex.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.client.entity.Client;
import ppex.client.process.DetectProcess;
import ppex.proto.type.ProbeTypeMsg;
import ppex.proto.type.TypeMessageHandler;
import ppex.utils.Constants;
import ppex.utils.MessageUtil;


public class ProbeTypeMsgHandler implements TypeMessageHandler {

    private Logger LOGGER = Logger.getLogger(ProbeTypeMsgHandler.class);

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx,DatagramPacket packet) throws Exception{
//        ProbeTypeMsg pmsg = JSON.parseObject(msg.getBody(),ProbeTypeMsg.class);
//        pmsg.setFromInetSocketAddress(packet.sender());
        ProbeTypeMsg pmsg = MessageUtil.packet2Probemsg(packet);
        if (pmsg.getType() == ProbeTypeMsg.Type.FROM_CLIENT.ordinal()){
            throw new Exception("Wrong ProbeTypeMsg:" + pmsg.toString());
        }else if (pmsg.getType() == ProbeTypeMsg.Type.FROM_SERVER1.ordinal()){
            handleClientFromServer1Msg(ctx,pmsg);
        } else if (pmsg.getType() == ProbeTypeMsg.Type.FROM_SERVER2_PORT1.ordinal()){
            handleClientFromServer2Port1Msg(ctx,pmsg);
        }else if (pmsg.getType() == ProbeTypeMsg.Type.FROM_SERVER2_PORT2.ordinal()){
            handleClientFromServer2Port2Msg(ctx,pmsg);
        }else{
            throw new Exception("Unknown ProbeTypeMsg:" + pmsg.toString());
        }
    }

    //client端处理消息
    private void handleClientFromServer1Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        LOGGER.info("client handle msg recv from s1:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.ONE.ordinal()) {
            if (msg.getFromInetSocketAddress().getHostString().equals(Client.getInstance().local_address) && msg.getFromInetSocketAddress().getPort() == Constants.PORT1) {
                DetectProcess.getInstance().isPublicNetwork = true;
            }else{
                DetectProcess.getInstance().NAT_ADDRESS_FROM_S1 = msg.getRecordInetSocketAddress();
            }
        }
    }

    private void handleClientFromServer2Port1Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        LOGGER.info("client handle msg recv from s2p1:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.ONE.ordinal()){
            DetectProcess.getInstance().NAT_ADDRESS_FROM_S2P1 = msg.getRecordInetSocketAddress();
            if (DetectProcess.getInstance().NAT_ADDRESS_FROM_S1.equals(DetectProcess.getInstance().NAT_ADDRESS_FROM_S2P1)){
                DetectProcess.getInstance().NAT_ADDRESS_SAME = true;
            }else{
                DetectProcess.getInstance().NAT_ADDRESS_SAME = false;
            }
        }
    }

    private void handleClientFromServer2Port2Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        LOGGER.info("client handle msg recv from s2p2:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.ONE.ordinal()){
            DetectProcess.getInstance().setOne_from_server2p2(true);
        }else if (msg.getStep() == ProbeTypeMsg.Step.TWO.ordinal()){
            DetectProcess.getInstance().setTwo_from_server2p2(true);
        }
    }

}
