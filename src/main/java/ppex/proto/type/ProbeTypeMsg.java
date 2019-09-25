package ppex.proto.type;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import ppex.client.entity.Client;
import ppex.utils.Constants;
import ppex.utils.Identity;

import java.net.InetSocketAddress;

public class ProbeTypeMsg implements TypeMessageHandler {

    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, TypeMessage msg) throws Exception {
        if (msg.getType() != TypeMessage.Type.MSG_TYPE_PROBE.ordinal())
            return;
        ProbeTypeMsg pmsg = JSON.parseObject(msg.getBody(), ProbeTypeMsg.class);
        System.out.println("handle probe msg:" + msg.toString() + " \n" + pmsg.toString());
        if (pmsg.getType() == Type.FROM_CLIENT.ordinal()) {
            if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal())
                throw new Exception("Identity wrong:" + pmsg.toString());
            else if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
                handleServer1FromClientMsg(ctx,pmsg);
            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
                handleServer2Port1FromClientMsg(ctx,pmsg);
            }else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()){
                handleServer2Port2FromClientMsg(ctx,pmsg);
            } else {
                throw new Exception("unknown probe msg!" + pmsg.toString());
            }
        } else if (pmsg.getType() == Type.FROM_SERVER1.ordinal()) {
            if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
                throw new Exception("Identity wrong:" + pmsg.toString());
            } else if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal()) {
                handleClientFromServer1Msg(ctx,pmsg);
            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
                handleServer2Port1FromServer1Msg(ctx,pmsg);
            } else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()){
                handleServer2Port2FromServer1Msg(ctx,pmsg);
            } else {
                throw new Exception("unknown probe msg" + pmsg.toString());
            }
        } else if (pmsg.getType() == Type.FROM_SERVER2_PORT1.ordinal()) {
            if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
                throw new Exception("Identity wrong:" + pmsg.toString());
            } else if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal()) {
                handleClientFromServer2Port1Msg(ctx,pmsg);
            } else if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()) {
                handleServer1FromServer2Port1Msg(ctx,pmsg);
            }else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()){
                handleServer2Port2FromServer2Port1Msg(ctx,pmsg);
            } else {
                throw new Exception("unknown probe msg" + pmsg.toString());
            }
        }else if (pmsg.getType() == Type.FROM_SERVER2_PORT2.ordinal()){
            if (Identity.INDENTITY == Identity.Type.SERVER2_PORT2.ordinal()){
                throw new Exception("Identity wrong:" + pmsg.toString());
            }else if (Identity.INDENTITY == Identity.Type.CLIENT.ordinal()){
                handleClientFromServer2Port2Msg(ctx,pmsg);
            }else if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal()){
                handleServer1FromServer2Port2Msg(ctx,pmsg);
            }else if (Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()){
                handleServer2Port1FromServer2Port2Msg(ctx,pmsg);
            }else{
                throw new Exception("unknown probe msg" + pmsg.toString());
            }
        }else{
            throw new Exception("Unknown TypeMessage:" + msg.toString());
        }
    }

    //client端处理消息
    private void handleClientFromServer1Msg(ChannelHandlerContext ctx,ProbeTypeMsg msg) {
        if (msg.getStep() == Step.ONE.ordinal()){
            if (msg.getInetSocketAddress().getHostString().equals(Client.getInstance().local_address) && msg.getInetSocketAddress().getPort() == Constants.PORT1){
                Client.getInstance().NAT_TYPE = Client.NATTYPE.PUBLIC_NETWORK.ordinal();
            }
        }
    }

    private void handleClientFromServer2Port1Msg(ChannelHandlerContext ctx,ProbeTypeMsg msg) {

    }

    private void handleClientFromServer2Port2Msg(ChannelHandlerContext ctx,ProbeTypeMsg msg){

    }

    //server1处理消息
    private void handleServer1FromClientMsg(ChannelHandlerContext ctx,ProbeTypeMsg msg) {
        if (msg.getStep() == Step.ONE.ordinal()){
            //向client发回去包,并且向server2:port1发包

        }
    }

    private void handleServer1FromServer2Port1Msg(ChannelHandlerContext ctx,ProbeTypeMsg msg) {

    }

    private void handleServer1FromServer2Port2Msg(ChannelHandlerContext ctx,ProbeTypeMsg msg){

    }

    //Server2:Port1处理消息
    private void handleServer2Port1FromClientMsg(ChannelHandlerContext ctx,ProbeTypeMsg msg) {

    }

    private void handleServer2Port1FromServer1Msg(ChannelHandlerContext ctx,ProbeTypeMsg msg) {

    }

    private void handleServer2Port1FromServer2Port2Msg(ChannelHandlerContext ctx,ProbeTypeMsg msg){

    }

    //Server2:Port2处理消息
    private void handleServer2Port2FromClientMsg(ChannelHandlerContext ctx,ProbeTypeMsg msg){

    }

    private void handleServer2Port2FromServer1Msg(ChannelHandlerContext ctx,ProbeTypeMsg msg){

    }

    private void handleServer2Port2FromServer2Port1Msg(ChannelHandlerContext ctx,ProbeTypeMsg msg){

    }




    public enum Type {
        FROM_CLIENT,
        FROM_SERVER1,
        FROM_SERVER2_PORT1,
        FROM_SERVER2_PORT2
    }

    public enum Step {
        ONE,
        TWO
    }

    public ProbeTypeMsg() {
    }

    public ProbeTypeMsg(int type, InetSocketAddress inetSocketAddress) {
        this.type = type;
        this.inetSocketAddress = inetSocketAddress;
    }

    /**
     * 消息从哪里来,是Type类型的值
     */
    private int type;
    /**
     * 第几阶段,看resources下的工作原理.md
     */
    private byte step;
    private InetSocketAddress inetSocketAddress;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    public byte getStep() {
        return step;
    }

    public void setStep(byte step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "ProbeTypeMsg{" +
                "type=" + type +
                ", inetSocketAddress=" + inetSocketAddress +
                '}';
    }
}
