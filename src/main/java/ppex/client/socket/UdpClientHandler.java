package ppex.client.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import ppex.proto.Message;
import ppex.proto.content.ContentMessage;
import ppex.proto.content.NormalContentMessage;
import ppex.utils.MessageUtil;


public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private ContentMessage contentMessage;

    public UdpClientHandler() {
        contentMessage = new NormalContentMessage();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        Message msg = MessageUtil.bytebuf2Msg(datagramPacket.content());
        if (msg != null){
            System.out.println("client recv:" + msg.toString() + " from:" + datagramPacket.sender());
        }else{
            System.out.println("client recv error");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
