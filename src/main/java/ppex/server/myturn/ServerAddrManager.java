package ppex.server.myturn;

import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAddrManager implements IAddrManager {
    private Map<InetSocketAddress, RudpPack> rudpPacks = new ConcurrentHashMap<>(30,0.9f);

    private static ServerAddrManager serverAddrManager = null;
    private ServerAddrManager(){}
    public static ServerAddrManager getInstance(){
        if (serverAddrManager == null)
            serverAddrManager = new ServerAddrManager();
        return serverAddrManager;
    }

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
    }

    @Override
    public Collection<RudpPack> getAll() {
       return rudpPacks.values();
    }
}
