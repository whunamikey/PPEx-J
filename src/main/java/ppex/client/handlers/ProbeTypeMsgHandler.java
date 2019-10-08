package ppex.client.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.client.entity.Client;
import ppex.client.process.DetectProcess;
import ppex.proto.type.ProbeTypeMsg;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMessageHandler;
import ppex.utils.Constants;


public class ProbeTypeMsgHandler implements TypeMessageHandler {

    private Logger LOGGER = Logger.getLogger(ProbeTypeMsgHandler.class);

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage msg, DatagramPacket packet) throws Exception{
        if (msg.getType() != TypeMessage.Type.MSG_TYPE_PROBE.ordinal())
            return;
        ProbeTypeMsg pmsg = JSON.parseObject(msg.getBody(),ProbeTypeMsg.class);
        pmsg.setFromInetSocketAddress(packet.sender());
        if (pmsg.getType() == ProbeTypeMsg.Type.FROM_CLIENT.ordinal()){
            throw new Exception("Wrong ProbeTypeMsg:" + msg.toString());
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
                Client.getInstance().NAT_TYPE = Client.NATTYPE.PUBLIC_NETWORK.ordinal();
                DetectProcess.getInstance().setStop(true);
            }else{
                Client.getInstance().STEP_ONE_NAT_ADDRESS = msg.getRecordInetSocketAddress();
            }
        }
    }

    private void handleClientFromServer2Port1Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        LOGGER.info("client handle msg recv from s2p1:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.ONE.ordinal()) {
            if (Client.getInstance().NAT_TYPE == Client.NATTYPE.PUBLIC_NETWORK.ordinal()) {
                DetectProcess.getInstance().setStop(true);
            } else {
                Client.getInstance().NAT_TYPE = Client.NATTYPE.FULL_CONE_NAT.ordinal();
                DetectProcess.getInstance().setStop(true);
            }
        }else if (msg.getStep() == ProbeTypeMsg.Step.TWO.ordinal()){
            if (!msg.getRecordInetSocketAddress().equals(Client.getInstance().STEP_ONE_NAT_ADDRESS)){
                Client.getInstance().NAT_TYPE = Client.NATTYPE.SYMMETIC_NAT.ordinal();
                DetectProcess.getInstance().setStop(true);
            }
        }

    }

    private void handleClientFromServer2Port2Msg(ChannelHandlerContext ctx, ProbeTypeMsg msg) {
        LOGGER.info("client handle msg recv from s2p2:" + msg.toString());
        if (msg.getStep() == ProbeTypeMsg.Step.TWO.ordinal()){
            Client.getInstance().NAT_TYPE = Client.NATTYPE.RESTRICT_CONE_NAT.ordinal();
            DetectProcess.getInstance().setStop(true);
        }
    }

}
