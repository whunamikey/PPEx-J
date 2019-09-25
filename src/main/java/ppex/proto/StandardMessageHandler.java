package ppex.proto;

import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.type.ProbeTypeMsg;
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
        handlers.put(TypeMessage.Type.MSG_TYPE_PROBE.ordinal(), new ProbeTypeMsg());
    }

    @Override
    public void handleDatagramPacket(DatagramPacket packet) throws Exception {
        try {
            LOGGER.info("Standard handle datagram packet");
            TypeMessage msg = MessageUtil.packet2Typemsg(packet);
            handlers.get(msg.getType()).handleTypeMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("handle datagram packet error" + e.getMessage());
        }
    }
}
