package ppex.socket.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import ppex.proto.Message;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("channel active");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        Message msg = Message.bytebuf2Msg(datagramPacket.content());
        if (msg != null){
            System.out.println("server recv msg:" + msg.toString());
            
            msg.setContent("msg from server");
            channelHandlerContext.writeAndFlush(new DatagramPacket(Message.msg2ByteBuf(msg),datagramPacket.sender()));
        }else{
            System.out.println("server recv msg error");
        }
    }
}
