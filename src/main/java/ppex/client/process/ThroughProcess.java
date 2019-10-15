package ppex.client.process;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.client.entity.Client;
import ppex.proto.entity.through.Connect;
import ppex.proto.entity.through.Connection;
import ppex.proto.type.ThroughTypeMsg;
import ppex.utils.MessageUtil;

import java.util.ArrayList;
import java.util.List;

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
            throughTypeMsg.setAction(ThroughTypeMsg.ACTION.SAVE_CONNINFO.ordinal());
            throughTypeMsg.setContent(JSON.toJSONString(Client.getInstance().localConnection));
            this.channel.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
            if (!channel.closeFuture().await(2000)) {
                System.out.println("查询超时");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getConnectionsFromServer(ChannelHandlerContext ctx) {
        LOGGER.info("client get ids from server");
        try {
            ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
            throughTypeMsg.setAction(ThroughTypeMsg.ACTION.GET_CONNINFO.ordinal());
            throughTypeMsg.setContent("");
            ctx.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectOtherPeer(ChannelHandlerContext ctx, Connection connection) {
        LOGGER.info("client connect other peer");
        try {
            ThroughTypeMsg throughTypeMsg = new ThroughTypeMsg();
            throughTypeMsg.setAction(ThroughTypeMsg.ACTION.CONNECT_CONN.ordinal());
            Connect connect = new Connect();
            connect.setType(Connect.TYPE.REQUEST_CONNECT_SERVER.ordinal());
            List<Connection> connections = new ArrayList<>();
            connections.add(Client.getInstance().localConnection);
            connections.add(connection);
            connect.setContent(JSON.toJSONString(connections));
            throughTypeMsg.setContent(JSON.toJSONString(connect));
            ctx.writeAndFlush(MessageUtil.throughmsg2Packet(throughTypeMsg, Client.getInstance().SERVER1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testConnectPeer(){
        try {

        }catch (Exception e){

        }
    }


}
