package ppex.client.socket;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;
import ppex.client.entity.Client;
import ppex.client.handlers.PongTypeMsgHandler;
import ppex.client.handlers.ProbeTypeMsgHandler;
import ppex.client.handlers.ThroughTypeMsgHandler;
import ppex.client.handlers.TxtTypeMsgHandler;
import ppex.proto.MessageHandler;
import ppex.proto.StandardMessageHandler;
import ppex.proto.type.PingTypeMsg;
import ppex.proto.type.TypeMessage;
import ppex.utils.MessageUtil;


public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static Logger LOGGER = Logger.getLogger(UdpClientHandler.class);

    private MessageHandler msgHandler;

    public UdpClientHandler() {
        msgHandler = new StandardMessageHandler();
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_PROBE.ordinal(), new ProbeTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_THROUGH.ordinal(), new ThroughTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_HEART_PONG.ordinal(),new PongTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_TXT.ordinal(),new TxtTypeMsgHandler());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        try {
            System.out.println("Client channel read0 from:" + datagramPacket.sender().toString());
            msgHandler.handleDatagramPacket(channelHandlerContext, datagramPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Message msg = MessageUtil.packet2Msg(datagramPacket);
//        if (msg != null){
//            System.out.println("client recv:" + msg.toString() + " from:" + datagramPacket.sender());
//        }else{
//            System.out.println("client recv error");
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE:
                    handleWriteIdle(ctx);
                    break;
                case READER_IDLE:
                    handleReadIdle();
                    break;
                case ALL_IDLE:
                    handleAllIdle();
                    break;
                default:
                    break;
            }
        }
    }

    private void handleWriteIdle(ChannelHandlerContext ctx){
        LOGGER.info("client handleWriteIdle");
        //心跳包
        PingTypeMsg pingTypeMsg = new PingTypeMsg();
//        pingTypeMsg.setType(PingTypeMsg.Type.HEART.ordinal());
//        pingTypeMsg.setContent(JSON.toJSONString(Client.getInstance().localConnection));
        ctx.writeAndFlush(MessageUtil.pingMsg2Packet(pingTypeMsg, Client.getInstance().SERVER1));
    }
    private void handleReadIdle(){
        LOGGER.info("client handleReadIdle");
    }
    private void handleAllIdle(){
        LOGGER.info("client handleAllIdle");
    }
}
