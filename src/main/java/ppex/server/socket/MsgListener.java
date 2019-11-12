package ppex.server.socket;

import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.msg.Message;
import ppex.proto.rudp.ResponseListener;
import ppex.proto.rudp.RudpPack;

public class MsgListener implements ResponseListener {
    private static Logger LOGGER = Logger.getLogger(MsgListener.class);

    @Override
    public void onResponse(ChannelHandlerContext ctx, RudpPack rudpPack, Message message) {
        LOGGER.info("MsgListener get msg:" + message.getMsgid() + " :" + message.getContent());
    }
}
