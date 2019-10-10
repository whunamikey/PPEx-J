package ppex.client.process;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import ppex.client.entity.Client;
import ppex.proto.entity.through.SAVEINFO;
import ppex.proto.type.ThroughTypeMsg;
import ppex.utils.MessageUtil;

public class ThroughProcess {

    private static ThroughProcess instance = null;

    private ThroughProcess() {
    }

    public static ThroughProcess getInstance() {
        if (instance == null)
            instance = new ThroughProcess();
        return instance;
    }

    private Channel channel;

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void sendSaveInfo() {
        ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
        throughTypeMsg.setAction(ThroughTypeMsg.ACTION.SAVE_INFO.ordinal());
        SAVEINFO saveinfo = new SAVEINFO();
        saveinfo.address = Client.getInstance().address;
        saveinfo.nattype = Client.getInstance().NAT_TYPE;
        saveinfo.peerName = Client.getInstance().peerName;
        saveinfo.id = Client.getInstance().id;
        throughTypeMsg.setContent(JSON.toJSONString(saveinfo));
        if (channel.isOpen()) {
            this.channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
        }
    }

    public void getIDSInfoFromServer() {
        ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
        throughTypeMsg.setAction(ThroughTypeMsg.ACTION.GET_INFO.ordinal());
        throughTypeMsg.setContent("");
        if (channel.isOpen()) {
            this.channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
        }
    }

    public void connectOthrePeer(long id) {
        ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
        throughTypeMsg.setAction(ThroughTypeMsg.ACTION.CONNECT.ordinal());
        throughTypeMsg.setContent("" + id);
        if (channel.isOpen()) {
            this.channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
        }
    }


}
