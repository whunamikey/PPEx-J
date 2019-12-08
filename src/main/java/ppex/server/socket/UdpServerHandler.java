package ppex.server.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;
import ppex.proto.msg.entity.Connection;
import ppex.proto.rudp.*;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private Logger LOGGER = Logger.getLogger(UdpServerHandler.class);

    public UdpServerHandler(Server server) {
        this.disruptorExectorPool = disruptorExectorPool;
        this.addrManager = addrManager;
        this.responseListener = responseListener;
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
//            Channel channel = channelHandlerContext.channel();
//            IMessageExecutor executor = disruptorExectorPool.getAutoDisruptorProcessor();
//            PcpPack pcpPack = channelManager.get(channel, datagramPacket);
//            if (pcpPack == null) {
//                Connection connection = new Connection("", datagramPacket.sender(), "", 0, channel);
//                PcpOutput pcpOutput = new ServerOutput();
//
//                pcpPack = new PcpPack(0x1, pcpListener, executor, connection, pcpOutput);
//                channelManager.New(channel, pcpPack);
//            }
//            pcpPack.read(datagramPacket.content());
//            ScheduleTask scheduleTask = new ScheduleTask(executor, pcpPack, channelManager);
//            DisruptorExectorPool.scheduleHashedWheel(scheduleTask, pcpPack.getInterval());

            //rudp测试
            LOGGER.info("UdpServer rcv packet from :" + datagramPacket.sender());
            RudpPack rudpPack = addrManager.get(datagramPacket.sender());
            if (rudpPack != null) {
                rudpPack.getConnection().setAddress(datagramPacket.sender());
                rudpPack.getConnection().setChannel(channelHandlerContext.channel());
                rudpPack.setCtx(channelHandlerContext);
                rudpPack.read(datagramPacket.content());
                return;
            }

            IMessageExecutor executor = disruptorExectorPool.getAutoDisruptorProcessor();
            Connection connection = new Connection("", datagramPacket.sender(), "", 0, channelHandlerContext.channel());
            Output output = new ServerOutput();
            rudpPack = new RudpPack(output, connection, executor, this.responseListener, channelHandlerContext);
            addrManager.New(datagramPacket.sender(), rudpPack);
            rudpPack.read(datagramPacket.content());
            RudpScheduleTask scheduleTask = new RudpScheduleTask(executor, rudpPack, addrManager);
            DisruptorExectorPool.scheduleHashedWheel(scheduleTask, rudpPack.getInterval());

            //2019-10-30修改.使用pcppack
//            msgHandler.handleDatagramPacket(channelHandlerContext, datagramPacket);

            //保留测试用
//            TypeMessage msg = MessageUtil.packet2Typemsg(datagramPacket);
//            ProbeTypeMsg pmsg = JSON.parseObject(msg.getBody(),ProbeTypeMsg.class);
//            pmsg.setFromInetSocketAddress(datagramPacket.sender());
//            if (pmsg.getStep() == ProbeTypeMsg.Step.ONE.ordinal()){
//                //向client发回去包,并且向server2:port1发包
//                //19-10-8优化,向Server2:Port2发包
//                pmsg.setType(ProbeTypeMsg.Type.FROM_SERVER1.ordinal());
//                pmsg.setRecordInetSocketAddress(pmsg.getFromInetSocketAddress());
//                pmsg.setFromInetSocketAddress(Server.getInstance().SERVER1);
////                channelHandlerContext.writeAndFlush(MessageUtil.probemsg2Packet(pmsg,pmsg.getRecordInetSocketAddress()));
////                channelHandlerContext.channel().writeAndFlush(new DatagramPacket())
//                DatagramPacket returnMsg = MessageUtil.probemsg2Packet(pmsg,datagramPacket.sender());
//                System.out.println("datagram sender:" + datagramPacket.sender().toString() + " record:" + pmsg.getRecordInetSocketAddress().toString() + " rece:" + datagramPacket.recipient().toString());
//                channelHandlerContext.writeAndFlush(returnMsg);
//                System.out.println("write to client");
//            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("---->ChannelRead0 exception:" + e.getCause());
            System.out.println("server recv msg error");
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
