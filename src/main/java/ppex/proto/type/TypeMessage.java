package ppex.proto.type;

import ppex.proto.Message;
import ppex.utils.MessageUtil;

public interface TypeMessage {
    default void handleTypeMessage(MessageUtil.MsgType type, String content){
        System.out.println("type:" + type + " content:" + content);
    }
}
