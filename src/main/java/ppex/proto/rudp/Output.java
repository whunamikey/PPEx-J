package ppex.proto.rudp;

import io.netty.buffer.ByteBuf;

public interface Output {
    default void output(ByteBuf data,Rudp rudp){}
}
