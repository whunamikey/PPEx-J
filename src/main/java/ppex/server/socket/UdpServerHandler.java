package ppex.server.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.log4j.Logger;
import ppex.server.myturn.Connection;
import ppex.server.myturn.Peer;
import ppex.proto.Message;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private Logger logger = Logger.getLogger(UdpServerHandler.class);

    private final Peer peer;

    public UdpServerHandler(Peer peer) {
        this.peer = peer;
    }

    private Attribute<Connection> getSessionAttribute(ChannelHandlerContext ctx) {
        return ctx.attr(AttributeKey.valueOf("session"));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("---->channel Active");
        final Connection connection = new Connection(ctx);
        getSessionAttribute(ctx).set(connection);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("---->channel inactive:" + ctx.channel().remoteAddress());
        final Connection connection = getSessionAttribute(ctx).get();
        peer.handleConnectionClosed(connection);
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
        peer.handleConnectionClosed(getSessionAttribute(ctx).get());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        Message msg = Message.bytebuf2Msg(datagramPacket.content());
        if (msg != null) {
            logger.warn("---->channelRead0:" + msg.toString() + " from :" + datagramPacket.sender());
            msg.setContent("server recv from" + datagramPacket.sender().toString());
            final Connection connection = getSessionAttribute(channelHandlerContext).get();
            connection.setChannelHandlerContext(channelHandlerContext, datagramPacket.sender());
            connection.sendMsg(msg);
//            channelHandlerContext.writeAndFlush(new DatagramPacket(Message.msg2ByteBuf(msg), datagramPacket.sender()));
        } else {
            logger.warn("---->channelRead0:Server Recv Msg Error");
            System.out.println("server recv msg error");
        }
    }
}
