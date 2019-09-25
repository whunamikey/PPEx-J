package ppex.proto.type;

import com.alibaba.fastjson.JSON;

import java.net.InetSocketAddress;

public class ProbeTypeMsg implements TypeMessageHandler{

    @Override
    public void handleTypeMessage(TypeMessage msg) {
        if (msg.getType() != TypeMessage.Type.MSG_TYPE_PROBE.ordinal())
            return;
        ProbeTypeMsg pmsg = JSON.parseObject(msg.getBody(),ProbeTypeMsg.class);
        System.out.println("handle probe msg:" + msg.toString() + " \n" + pmsg.toString());
    }

    public enum Type{
        FROM_CLIENT,
        FROM_SERVER1,
        FROM_SERVER2
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

    @Override
    public String toString() {
        return "ProbeTypeMsg{" +
                "type=" + type +
                ", inetSocketAddress=" + inetSocketAddress +
                '}';
    }
}
