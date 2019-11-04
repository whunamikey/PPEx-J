package ppex.client.socket;

import io.netty.channel.Channel;
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
import ppex.proto.msg.MessageHandler;
import ppex.proto.msg.StandardMessageHandler;
import ppex.proto.msg.entity.Connection;
import ppex.proto.msg.type.PingTypeMsg;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.pcp.*;
import ppex.utils.Constants;
import ppex.utils.MessageUtil;
import ppex.utils.tpool.DisruptorExectorPool;
import ppex.utils.tpool.IMessageExecutor;


public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static Logger LOGGER = Logger.getLogger(UdpClientHandler.class);

    private MessageHandler msgHandler;

    private PcpListener pcpListener;
    private DisruptorExectorPool disruptorExectorPool;
    private IChannelManager channelManager;

    public UdpClientHandler(PcpListener pcpListener, DisruptorExectorPool disruptorExectorPool, IChannelManager channelManager) {
        msgHandler = StandardMessageHandler.New();
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_PROBE.ordinal(), new ProbeTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_THROUGH.ordinal(), new ThroughTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_HEART_PONG.ordinal(),new PongTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_TXT.ordinal(),new TxtTypeMsgHandler());

        this.pcpListener = pcpListener;
        this.disruptorExectorPool = disruptorExectorPool;
        this.channelManager = channelManager;

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        try {
            Channel channel = channelHandlerContext.channel();
            LOGGER.info("ClientHandler channel local:" + channel.localAddress() + " remote:" + channel.remoteAddress());
            Ukcp ukcp= channelManager.get(channel,datagramPacket.sender());
            if (ukcp == null){
                Connection connection = new Connection("",datagramPacket.sender(),"From1", Constants.NATTYPE.UNKNOWN.ordinal(),channel);
                IMessageExecutor executor = disruptorExectorPool.getAutoDisruptorProcessor();
                PcpOutput pcpOutput = new ClientOutput();
                ukcp = new Ukcp(pcpOutput,null,executor,connection);
                channelManager.New(channel,ukcp);
            }
            ukcp.read(datagramPacket.content());

//            msgHandler.handleDatagramPacket(channelHandlerContext, datagramPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
