package ppex.proto.type;

import ppex.utils.MessageUtil;

public class DetectMessage implements TypeMessage {

    @Override
    public void handleTypeMessage(MessageUtil.MsgType type, String content) {
        if (type != MessageUtil.MsgType.MSG_TYPE_DETECT)
            return;
    }
}
