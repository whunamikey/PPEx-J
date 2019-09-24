package ppex.myturn;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import ppex.proto.Message;

import java.net.InetSocketAddress;

public class ConnectionService {
    private InetSocketAddress inetSocketAddress;
    private ChannelHandlerContext ctx;
    private String peerName;

    public ConnectionService(ChannelHandlerContext ctx) {
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
}
