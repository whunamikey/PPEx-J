package ppex.proto;

import io.netty.channel.socket.DatagramPacket;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMessageHandler;

import java.util.HashMap;
import java.util.Map;

public class NormalMessageHandler implements MessageHandler {

    private Map<TypeMessage.Type, TypeMessageHandler> typeMsgHandlers = new HashMap<>(5,0.8f);

    public NormalMessageHandler() {
        init();
    }

    private void init(){
    }

    @Override
    public void handleDatagramPacket(DatagramPacket packet) {
//        TypeMessage typeMsg = JSON.parseObject(content, TypeMessage.class);
//        typeMsgHandlers.get(typeMsg.getType()).handleTypeMessage(typeMsg);


    }
}
