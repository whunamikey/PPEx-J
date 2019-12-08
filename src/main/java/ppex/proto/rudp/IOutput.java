package ppex.proto.rudp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;


public interface IOutput {
    void update(Channel channel);
    void output(ByteBuf data, Rudp rudp, long sn);
}
