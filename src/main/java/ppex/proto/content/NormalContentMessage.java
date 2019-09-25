package ppex.proto.content;

import ppex.proto.Message;
import ppex.proto.type.DetectMessage;
import ppex.proto.type.TypeMessage;
import ppex.utils.MessageUtil;

import java.util.HashMap;
import java.util.Map;

public class NormalContentMessage implements ContentMessage {

    private Map<MessageUtil.MsgType,TypeMessage> typeMsgs = new HashMap<>(5,0.8f);

    public NormalContentMessage() {
        init();
    }

    private void init(){
        typeMsgs.put(MessageUtil.MsgType.MSG_TYPE_DETECT,new DetectMessage());
    }

    @Override
    public void handleContent(String content) {

    }
}
