package ppex.server.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.Message;
import ppex.proto.MessageHandler;
import ppex.proto.NormalMessageHandler;
import ppex.utils.MessageUtil;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private Logger logger = Logger.getLogger(UdpServerHandler.class);


    private MessageHandler msgHandler;

    public UdpServerHandler() {
        msgHandler = new NormalMessageHandler();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("---->channel Active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("---->channel inactive:" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        Message msg = MessageUtil.packet2Msg(datagramPacket);
        if (msg != null) {
            logger.warn("---->channelRead0:" + msg.toString() + " from :" + datagramPacket.sender());
            msg.setContent("server recv from" + datagramPacket.sender().toString());
            msgHandler.handleDatagramPacket(datagramPacket);
            //            channelHandlerContext.writeAndFlush(new DatagramPacket(Message.msg2ByteBuf(msg), datagramPacket.sender()));
        } else {
            logger.warn("---->channelRead0:Server Recv Msg Error");
            System.out.println("server recv msg error");
        }
    }
}
