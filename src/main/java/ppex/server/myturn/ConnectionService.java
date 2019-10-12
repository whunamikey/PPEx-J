package ppex.server.myturn;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import ppex.utils.Constants;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ConnectionService {

    private static Logger LOGGER = Logger.getLogger(ConnectionService.class);

    private static ConnectionService instance = null;

    private ConnectionService() {
    }

    public static ConnectionService getInstance() {
        if (instance == null)
            instance = new ConnectionService();
        return instance;
    }

    //server id ->connection
    private final Map<Long, Connection> connections = new HashMap<>(10, 0.9f);

    public boolean addConnection(final Connection connection) {
        final String peerName = connection.getPeerName();
        final Connection previousConnection = connections.put(connection.getId(), connection);
        if (previousConnection != null) {
            //todo 关闭之前的connection
            System.out.println("Already existing connection to " + peerName + " is closed.");
        }
        return true;
    }

    public boolean hasConnection(long id) {
        return connections.containsKey(id);
    }

    public String getAllConnectionId() {
        List<Long> ids = connections.keySet().stream().sorted().collect(Collectors.toList());
        return JSON.toJSONString(ids);
    }

    public List<Long> getAllConnectionIds() {
        return connections.keySet().stream().sorted().collect(Collectors.toList());
    }

    public boolean removeConnection(final Connection connection) {
        final boolean removed = connections.remove(connection.getId()) != null;
        if (removed) {
            System.out.println(connection.getPeerName() + " connection is removed from connections.");
        } else {
            System.out.println(connection.getPeerName() + " is not removed since not found in connections.");
        }
        return removed;
    }

    public int getNumberOfConnection() {
        return connections.size();
    }

    public boolean isConnectedTo(final long peerId) {
        return connections.containsKey(peerId);
    }

    public Connection getConnection(final long peerId) {
        return connections.get(peerId);
    }

    public Collection<Connection> getConnections() {
        return Collections.unmodifiableCollection(connections.values());
    }

    public void connectTo(String host, int port, CompletableFuture<Void> futureNotify) {

    }



    public void connectPeers(long from, long to) {
        if (!connections.containsKey(from) || !connections.containsKey(to)) {
            return;
        }
        handleConnectPeers(connections.get(from), connections.get(to));
    }

    private void handleConnectPeers(Connection A, Connection B) {
        switch (Constants.NATTYPE.getByValue(B.getNATTYPE())){
            case UNKNOWN:
                break;
            case PUBLIC_NETWORK:
                //当B是公网时,直接让A发信息给B.回信息给A,带过去B的地址,让A往B发信息
                break;
            case FULL_CONE_NAT:
            case RESTRICT_CONE_NAT:
                //当B是FullConeNat和RestrictConeNat时,打洞.
                //即服务将A的信息返回给B.B用得到A的地址给A发信息.这个操作要在A给B发信息之前进行.不然A如果发发送信息给B接收不到.
                //B给A发信息之后,A就可以用得到B的地址给B发信息.
                break;
            case PORT_RESTRICT_CONE_NAT:
                //当B是PortRestrictConeNAT时,只有A是SymmeticNat时候走平台转发,其它都是打洞,打洞参考FullConeNat和RestrictConeNat
                if (A.getNATTYPE() == Constants.NATTYPE.SYMMETIC_NAT.getValue()){

                }else{

                }
                break;
            case SYMMETIC_NAT:
                //当B是SymmeticNat时,只有当A是公网,FullConeNat,RestricConeNat时,采用反向穿越.剩下A是PortRrestrictConeNat和SymmeticNat时,走平台转发
                if (A.getNATTYPE() > Constants.NATTYPE.RESTRICT_CONE_NAT.getValue()){
                    //平台转发
                }else{
                    //反向穿越.即服务给A发回信息,A得到B的信息,要给B先发送包..然后B再给A发送包.
                }
                break;
        }
    }


}
