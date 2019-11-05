package ppex.proto.rudp;

import ppex.proto.msg.Message;

public interface ResponseListener {
    default void onResponse(Message message){}
}
