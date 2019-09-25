package ppex.client.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import ppex.proto.Message;
import ppex.proto.MessageHandler;
import ppex.proto.NormalMessageHandler;
import ppex.utils.MessageUtil;


public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private MessageHandler contentMessage;

    public UdpClientHandler() {
        contentMessage = new NormalMessageHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        Message msg = MessageUtil.packet2Msg(datagramPacket);
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
