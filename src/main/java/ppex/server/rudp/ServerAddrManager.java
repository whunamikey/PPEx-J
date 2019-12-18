package ppex.server.rudp;

import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAddrManager implements IAddrManager {
    private Map<InetSocketAddress, RudpPack> rudpPacks = new ConcurrentHashMap<>(30,0.9f);

    @Override
    public RudpPack get(InetSocketAddress sender) {
        return rudpPacks.get(sender);
    }

    @Override
    public void New(InetSocketAddress sender, RudpPack rudpPack) {
        rudpPacks.put(sender,rudpPack);
    }

    @Override
    public void Del(RudpPack rudpPack) {
        rudpPacks.entrySet().removeIf(entry -> entry.getValue().equals(rudpPack));
    }

    @Override
    public Collection<RudpPack> getAll() {
       return rudpPacks.values();
    }

    @Override
    public Set<Map.Entry<InetSocketAddress, RudpPack>> getAllEntry() {
        return rudpPacks.entrySet();
    }
}
