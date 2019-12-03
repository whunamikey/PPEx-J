package ppex.proto.msg;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;

import java.util.HashMap;
import java.util.Map;

public class StandardMessageHandler implements MessageHandler {

    private Logger LOGGER = Logger.getLogger(StandardMessageHandler.class);
    private Map<Integer, TypeMessageHandler> handlers;

    private StandardMessageHandler() {
        init();
    }

    public static StandardMessageHandler New() {
        return new StandardMessageHandler();
    }

    private void init() {
        handlers = new HashMap<>(10, 0.9f);
    }

    public void addTypeMessageHandler(Integer type, TypeMessageHandler handler) {
        if (handlers != null) {
            handlers.put(type, handler);
        }
    }


    @Override
    public void handleMessage(ChannelHandlerContext ctx, RudpPack rudpPack, IAddrManager addrManager, Message msg) {
        try {
            TypeMessage tmsg = JSON.parseObject(msg.getContent(), TypeMessage.class);
            handlers.get(tmsg.getType()).handleTypeMessage(ctx,rudpPack,addrManager, tmsg);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("StandardMessageHandler handle message error:" + e.getCause().toString());
        }
    }
}
