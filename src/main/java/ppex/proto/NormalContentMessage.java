package ppex.proto;

import com.alibaba.fastjson.JSON;
import ppex.proto.type.ProbeMessage;
import ppex.proto.type.TypeMessage;
import ppex.proto.type.TypeMsg;
import ppex.utils.MessageUtil;

import java.util.HashMap;
import java.util.Map;

public class NormalContentMessage implements ContentMessage {

    private Map<MessageUtil.MsgType,TypeMessage> typeMsgHandlers = new HashMap<>(5,0.8f);

    public NormalContentMessage() {
        init();
    }

    private void init(){
        typeMsgHandlers.put(MessageUtil.MsgType.MSG_TYPE_PROBE,new ProbeMessage());
    }

    @Override
    public void handleContent(String content) {
        TypeMsg typeMsg = JSON.parseObject(content, TypeMsg.class);
        typeMsgHandlers.get(typeMsg.getType()).handleTypeMessage(typeMsg);
    }
}
