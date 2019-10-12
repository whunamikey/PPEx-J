package ppex.client.process;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.client.entity.Client;
import ppex.proto.entity.through.CONNECT;
import ppex.proto.entity.through.SAVEINFO;
import ppex.proto.type.ThroughTypeMsg;
import ppex.utils.MessageUtil;

public class ThroughProcess {

    private static Logger LOGGER = Logger.getLogger(ThroughProcess.class);

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
        LOGGER.info("client send save info");
        try {
            ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
            throughTypeMsg.setAction(ThroughTypeMsg.ACTION.SAVE_INFO.ordinal());
            SAVEINFO saveinfo = new SAVEINFO();
            saveinfo.address = Client.getInstance().address;
            saveinfo.nattype = Client.getInstance().NAT_TYPE;
            saveinfo.peerName = Client.getInstance().peerName;
            saveinfo.id = Client.getInstance().id;
            throughTypeMsg.setContent(JSON.toJSONString(saveinfo));
            this.channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
            if (!channel.closeFuture().await(2000)) {
                System.out.println("查询超时");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getIDSInfoFromServer(ChannelHandlerContext ctx) {
        LOGGER.info("client get ids from server");
        try {
            ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
            throughTypeMsg.setAction(ThroughTypeMsg.ACTION.GET_INFO.ordinal());
            throughTypeMsg.setContent("");
            ctx.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectOthrePeer(ChannelHandlerContext ctx, long id) {
        LOGGER.info("client connect other peer");
        try {
            ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
            throughTypeMsg.setAction(ThroughTypeMsg.ACTION.CONNECT.ordinal());
            CONNECT connect = new CONNECT();
            connect.setType(CONNECT.TYPE.REQUEST_CONNECT.ordinal());
            connect.setFrom(1);
            connect.setTo(2);
            throughTypeMsg.setContent(JSON.toJSONString(connect));
            ctx.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
