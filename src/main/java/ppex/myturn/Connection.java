package ppex.myturn;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import ppex.proto.Message;

import java.net.InetSocketAddress;

public class Connection {
    private InetSocketAddress inetSocketAddress;
    private ChannelHandlerContext ctx;
    private String peerName;

    public Connection(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public InetSocketAddress getInetSocketAddress(){
        return this.inetSocketAddress;
    }

    public void sendMsg(Message msg){
        if (ctx != null && ctx.isRemoved()){
            ctx.writeAndFlush(new DatagramPacket(Message.msg2ByteBuf(msg),inetSocketAddress));
        }else{
            System.out.println("can not send msg to " + peerName);
        }
    }

    public void close(){
        if (ctx != null){
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
                "inetSocketAddress=" + inetSocketAddress +
                ", ctx=" + ctx +
                ", peerName='" + peerName + '\'' +
                '}';
    }
}
