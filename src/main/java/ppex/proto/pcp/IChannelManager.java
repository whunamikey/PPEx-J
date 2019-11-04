package ppex.proto.pcp;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface IChannelManager {
//    PcpPack get(Channel channel, DatagramPacket msg);
//    PcpPack get(Channel channel, InetSocketAddress sender);
//    void New(Channel channel,PcpPack pcpPack);
//    void Del(PcpPack pcpPack);
//    Collection<PcpPack> getAll();
    Ukcp get(Channel channel, DatagramPacket msg);
    Ukcp get(Channel channel,InetSocketAddress sender);
    void New(Channel channel,Ukcp ukcp);
    void Del(Ukcp ukcp);
    Collection<Ukcp> getAll();
}
