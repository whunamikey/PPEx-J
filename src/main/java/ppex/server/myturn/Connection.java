package ppex.server.myturn;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.Message;
import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;

public class Connection {

    private Logger logger = Logger.getLogger(Connection.class);

    private InetSocketAddress inetSocketAddress;
    private ChannelHandlerContext ctx;
    private String peerName;

    public Connection(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        if (null != ctx.channel() && null != ctx.channel().remoteAddress()) {
            this.inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            logger.info("---->New:inetSocketAddress:" + inetSocketAddress.toString());
        }
    }

    public void setChannelHandlerContext(ChannelHandlerContext ctx,InetSocketAddress inetSocketAddress) {
        this.ctx = ctx;
        this.inetSocketAddress = inetSocketAddress;
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
            logger.info("can not send msg to " + peerName);
        }
    }

    public void close() {
        if (ctx != null) {
            ctx.close();
            ctx = null;
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