package ppex.proto.pcp;

import io.netty.buffer.ByteBuf;
import ppex.proto.msg.Message;

public interface PcpListener {
    default void onResponse(Message message){}
    default void onResponse(ByteBuf byteBuf){}
}
