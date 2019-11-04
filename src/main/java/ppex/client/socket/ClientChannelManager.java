package ppex.client.socket;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import ppex.proto.pcp.IChannelManager;
import ppex.proto.pcp.PcpPack;
import ppex.proto.pcp.Ukcp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientChannelManager implements IChannelManager {

    private ClientChannelManager(){}
    public static ClientChannelManager New(){
        return new ClientChannelManager();
    }

//    @Override
//    public PcpPack get(Channel channel, DatagramPacket msg) {
//        return get(channel,msg.sender());
//    }
//
//    @Override
//    public PcpPack get(Channel channel, InetSocketAddress sender) {
//        return pcpPackMap.get(sender);
//    }
//
//    @Override
//    public void New(Channel channel, PcpPack pcpPack) {
//        pcpPackMap.put(channel.localAddress(),pcpPack);
//    }
//
//    @Override
//    public void Del(PcpPack pcpPack) {
//
//    }
//
//    @Override
//    public Collection<PcpPack> getAll() {
//        return this.pcpPackMap.values();
//    }
        private Map<SocketAddress, Ukcp> ukcps = new ConcurrentHashMap<>(30,0.9f);

    @Override
    public Ukcp get(Channel channel, DatagramPacket msg) {
        return get(channel,msg.sender());
    }

    @Override
    public Ukcp get(Channel channel, InetSocketAddress sender) {
        return ukcps.get(sender);
    }

    @Override
    public void New(Channel channel, Ukcp ukcp) {
        ukcps.put(channel.localAddress(),ukcp);
    }

    @Override
    public void Del(Ukcp ukcp) {
    }

    @Override
    public Collection<Ukcp> getAll() {
        return null;
    }
}
