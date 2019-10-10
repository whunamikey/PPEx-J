package ppex.server.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.MessageHandler;
import ppex.proto.StandardMessageHandler;
import ppex.proto.type.TypeMessage;
import ppex.server.handlers.ProbeTypeMsgHandler;
import ppex.server.handlers.ThroughTypeMsgHandler;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private Logger logger = Logger.getLogger(UdpServerHandler.class);


    private MessageHandler msgHandler;

    public UdpServerHandler() {
        msgHandler = new StandardMessageHandler();
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_PROBE.ordinal(),new ProbeTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_THROUGH.ordinal(),new ThroughTypeMsgHandler());
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
        try {
            msgHandler.handleDatagramPacket(channelHandlerContext,datagramPacket);

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
        }catch (Exception e){
            logger.warn("---->ChannelRead0 exception:" + e.getMessage());
            System.out.println("server recv msg error");
        }
    }
}
