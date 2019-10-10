package ppex.proto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMessageHandler;
import ppex.utils.MessageUtil;

import java.util.HashMap;
import java.util.Map;

public class StandardMessageHandler implements MessageHandler {

    private Logger LOGGER = Logger.getLogger(StandardMessageHandler.class);
    private Map<Integer, TypeMessageHandler> handlers;

    public StandardMessageHandler() {
        init();
    }

    private void init() {
        handlers = new HashMap<>(10,0.9f);
    }

    public void addTypeMessageHandler(Integer type,TypeMessageHandler handler){
        if (handlers != null){
            handlers.put(type,handler);
        }
    }

    @Override
    public void handleDatagramPacket(ChannelHandlerContext ctx,DatagramPacket packet) throws Exception {
        try {
            LOGGER.info("Standard handle datagram packet");
            TypeMessage msg = MessageUtil.packet2Typemsg(packet);
            handlers.get(msg.getType()).handleTypeMessage(ctx,packet);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("handle datagram packet error" + e.getMessage());
        }
    }
}
