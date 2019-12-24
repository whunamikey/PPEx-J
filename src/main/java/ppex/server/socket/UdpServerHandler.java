package ppex.server.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;
import ppex.proto.entity.Connection;
import ppex.proto.rudp.IOutput;
import ppex.proto.rudp.RudpPack;
import ppex.server.rudp.ServerOutput;
import ppex.utils.NatTypeUtil;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private Logger LOGGER = Logger.getLogger(UdpServerHandler.class);
    private Server server;

    public UdpServerHandler(Server server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LOGGER.info("---->channel Active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("---->channel inactive:" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        try {
            Channel channel = channelHandlerContext.channel();
            RudpPack rudpPack = server.getAddrManager().get(datagramPacket.sender());
            if (rudpPack != null){
                rudpPack.getOutput().update(channel);
                rudpPack.getOutput().getConn().setAddress(datagramPacket.sender());
                rudpPack.rcv2(datagramPacket.content());
                return;
            }

            Connection connection = new Connection("Unknown",datagramPacket.sender(),"Unknown", NatTypeUtil.NatType.UNKNOWN.getValue());
            IOutput output = new ServerOutput(channel,connection);
//            rudpPack = new RudpPack(output,server.getExecutor(),server.getResponseListener());
            rudpPack = RudpPack.newInstance(output,server.getExecutor(),server.getResponseListener(),server.getAddrManager());
            server.getAddrManager().New(datagramPacket.sender(),rudpPack);
            rudpPack.rcv2(datagramPacket.content());

//            RudpScheduleTask scheduleTask = new RudpScheduleTask(server.getExecutor(),rudpPack,server.getAddrManager());
//            server.getExecutor().executeTimerTask(scheduleTask,rudpPack.getInterval());

        } catch (Exception e) {
            System.out.println("server recv msg error");
            e.printStackTrace();
            LOGGER.error("---->ChannelRead0 exception:" + e.getCause());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case ALL_IDLE:
                    handleAllIdleEvent(ctx, e);
                    break;
                case READER_IDLE:
                    handleReadIdleEvent(ctx, e);
                    break;
                case WRITER_IDLE:
                    handleWriteIdleEvent(ctx, e);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleAllIdleEvent(ChannelHandlerContext ctx, IdleStateEvent e) {
        LOGGER.info("server all idleEvent");
    }

    private void handleReadIdleEvent(ChannelHandlerContext ctx, IdleStateEvent e) {
        LOGGER.info("server read idleEvent");
    }

    private void handleWriteIdleEvent(ChannelHandlerContext ctx, IdleStateEvent e) {
        LOGGER.info("server write idleEvent");
    }

}
