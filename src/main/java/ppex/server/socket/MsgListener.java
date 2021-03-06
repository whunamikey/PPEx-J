package ppex.server.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppex.proto.Statistic;
import ppex.proto.msg.Message;
import ppex.proto.msg.MessageHandler;
import ppex.proto.msg.StandardMessageHandler;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.ResponseListener;
import ppex.proto.rudp.RudpPack;
import ppex.proto.tpool.IThreadExecute;
import ppex.server.handlers.*;

public class MsgListener implements ResponseListener {
    private static Logger LOGGER = LoggerFactory.getLogger(MsgListener.class);

    private MessageHandler msgHandler;
    private IAddrManager addrManager;
    private IThreadExecute executor;

    public MsgListener(IAddrManager addrManager, IThreadExecute executor) {
        msgHandler = StandardMessageHandler.New();
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_PROBE.ordinal(), new ProbeTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_THROUGH.ordinal(), new ThroughTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_HEART_PING.ordinal(), new PingTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_FILE.ordinal(), new FileTypeMsgHandler());
        ((StandardMessageHandler) msgHandler).addTypeMessageHandler(TypeMessage.Type.MSG_TYPE_TXT.ordinal(), new TxtTypeMsgHandler());
        this.addrManager = addrManager;
        this.executor = executor;
    }

    @Override
    public void onResponse(RudpPack rudpPack, Message message) {
        msgHandler.handleMessage(rudpPack,addrManager,message);
//        LOGGER.info("onresponse msg :" +message.getMsgid());
        Statistic.responseCount.getAndIncrement();
    }
}
