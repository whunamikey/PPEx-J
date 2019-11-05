package ppex.proto.msg.type;

import io.netty.channel.ChannelHandlerContext;
import ppex.proto.rudp.RudpPack;

import java.net.InetSocketAddress;

public interface TypeMessageHandler {
    void handleTypeMessage(RudpPack rudpPack,TypeMessage tmsg);
}
