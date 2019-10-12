package ppex.server.myturn;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.Message;
import ppex.proto.entity.through.SAVEINFO;
import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;

public class Connection {

    private static Logger LOGGER = Logger.getLogger(Connection.class);

    private long id;                                    //暂时用id来识别每一个connection
    private InetSocketAddress inetSocketAddress;
    private ChannelHandlerContext ctx;
    private String peerName;
    private int NATTYPE;

    public Connection(SAVEINFO saveinfo){
        this.NATTYPE = saveinfo.nattype;
        this.id = saveinfo.id;
        this.peerName = saveinfo.peerName;
        this.inetSocketAddress = saveinfo.address;
    }

    public Connection(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        if (null != ctx.channel() && null != ctx.channel().remoteAddress()) {
            this.inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            LOGGER.info("---->New:inetSocketAddress:" + inetSocketAddress.toString());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNATTYPE() {
        return NATTYPE;
    }

    public void setNATTYPE(int NATTYPE) {
        this.NATTYPE = NATTYPE;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public InetSocketAddress getInetSocketAddress() {
        return this.inetSocketAddress;
    }

    public void sendMsg(Message msg) {
        if (ctx != null) {
            ctx.writeAndFlush(new DatagramPacket(MessageUtil.msg2ByteBuf(msg), inetSocketAddress));
        } else {
            LOGGER.info("can not send msg to " + peerName);
        }
    }

    public String getPeerName() {
        return peerName;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Connection that = (Connection) obj;
        return !(peerName != null ? !peerName.equals(that.peerName) : that.peerName != null);
    }

    @Override
    public String toString() {
        return "Connection{" +
                "inetSocketAddress=" + inetSocketAddress.toString() +
                ", ctx=" + ctx +
                ", peerName='" + peerName + '\'' +
                '}';
    }
}
