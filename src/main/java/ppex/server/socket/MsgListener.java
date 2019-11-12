package ppex.server.socket;

import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.msg.Message;
import ppex.proto.msg.MessageHandler;
import ppex.proto.msg.StandardMessageHandler;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.ResponseListener;
import ppex.proto.rudp.RudpPack;
import ppex.server.handlers.*;

public class MsgListener implements ResponseListener {
    private static Logger LOGGER = Logger.getLogger(MsgListener.class);

    private MessageHandler msgHandler;
    private IAddrManager addrManager;

    public MsgListener(IAddrManager addrManager) {
        msgHandler = StandardMessageHandler.New();
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_PROBE.ordinal(), new ProbeTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_THROUGH.ordinal(), new ThroughTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_HEART_PING.ordinal(), new PingTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_FILE.ordinal(), new FileTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_TXT.ordinal(), new TxtTypeMsgHandler());
        this.addrManager = addrManager;
    }

    @Override
    public void onResponse(ChannelHandlerContext ctx, RudpPack rudpPack, Message message) {
        LOGGER.info("MsgListener get msg:" + message.getMsgid() + " :" + message.getContent());
        msgHandler.handleMessage(ctx,rudpPack,addrManager,message);
    }
}
