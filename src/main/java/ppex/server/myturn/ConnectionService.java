package ppex.server.myturn;

import org.apache.log4j.Logger;
import ppex.proto.entity.through.Connection;
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
    private Map<String, Connection> connections = new HashMap<>(10, 0.9f);
    //保存需要申请转发的Connection
    private Map<String,Connection> forwardConnections = new HashMap<>(10,0.9f);

    public boolean addConnection(final Connection connection) {
        final String peerName = connection.getPeerName();
        final Connection previousConnection = connections.put(connection.getMacAddress(), connection);
        if (previousConnection != null) {
            //todo 关闭之前的connection
            System.out.println("Already existing connection to " + peerName + " is closed.");
        }
        return true;
    }

    public List<Connection> getAllConnections(){
        return connections.values().stream().collect(Collectors.toList());
    }

    public boolean hasConnection(Connection connection){
        return hasConnection(connection.getMacAddress());
    }
    public boolean hasConnection(String macAddress){
        return connections.containsKey(macAddress);
    }

    public boolean removeConnection(final Connection connection) {
        final boolean removed = connections.remove(connection.getMacAddress()) != null;
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

    public Collection<Connection> getConnections() {
        return Collections.unmodifiableCollection(connections.values());
    }

    public void connectTo(String host, int port, CompletableFuture<Void> futureNotify) {

    }
//    public ConnectResult connectTo(Connection A,Connection B){
//        return handleConnectPeers(A,B);
//    }
//
//    //组织一个数据结构ConnectResult,里面放着A的连接和B的连接
//    private ConnectResult handleConnectPeers(Connection A, Connection B) {
//        ConnectResult connectResult = new ConnectResult();
//        List<ConnectType> results = new ArrayList<>();
//        ConnectType typeA = new ConnectType();
//        ConnectType typeB = new ConnectType();
//        typeA.source = A;
//        typeA.target = B;
//        typeB.source = B;
//        typeB.target = A;
//        switch (Constants.NATTYPE.getByValue(B.natType)){
//            case UNKNOWN:
//                break;
//            case PUBLIC_NETWORK:
//                //当B是公网时,直接让A发信息给B.回信息给A,带过去B的地址,让A往B发信息
//                typeA.connectType = ConnectType.Type.DIRECT_SEND.ordinal();
//                typeB.connectType = ConnectType.Type.WAIT_DIRECT_SEND.ordinal();
//                break;
//            case FULL_CONE_NAT:
//            case RESTRICT_CONE_NAT:
//                //当B是FullConeNat和RestrictConeNat时,打洞.
//                //B往A的公网地址发信息后,也往服务发送打洞消息,然后服务转给A,A开始往B发送信息.
//                //即服务将A的信息返回给B.B用得到A的地址给A发信息.这个操作要在A给B发信息之前进行.不然A如果发发送信息给B接收不到.
//                //B给A发信息之后,A就可以用得到B的地址给B发信息.
//                typeA.connectType = ConnectType.Type.WAIT_PUNCH.ordinal();
//                typeB.connectType = ConnectType.Type.START_PUNCH.ordinal();
//                break;
//            case PORT_RESTRICT_CONE_NAT:
//                //当B是PortRestrictConeNAT时,只有A是SymmeticNat时候走平台转发,其它都是打洞,打洞参考FullConeNat和RestrictConeNat
//                if (A.natType == Constants.NATTYPE.SYMMETIC_NAT.getValue()){
//                    typeA.connectType = ConnectType.Type.PLATFORM_FORWARD.ordinal();
//                    typeB.connectType = ConnectType.Type.PLATFORM_FORWARD.ordinal();
//                }else{
//                    typeA.connectType = ConnectType.Type.WAIT_PUNCH.ordinal();
//                    typeB.connectType = ConnectType.Type.START_PUNCH.ordinal();
//                }
//                break;
//            case SYMMETIC_NAT:
//                //当B是SymmeticNat时,只有当A是公网,FullConeNat,RestricConeNat时,采用反向穿越.剩下A是PortRrestrictConeNat和SymmeticNat时,走平台转发
//                if (A.natType > Constants.NATTYPE.RESTRICT_CONE_NAT.getValue()){
//                    //平台转发
//                    typeA.connectType = ConnectType.Type.PLATFORM_FORWARD.ordinal();
//                    typeB.connectType = ConnectType.Type.PLATFORM_FORWARD.ordinal();
//                }else{
//                    //反向穿越.即服务给A发回信息,A得到B的信息,要给B先发送包..然后B再给A发送包.
//                    typeA.connectType = ConnectType.Type.START_PUNCH.ordinal();
//                    typeB.connectType = ConnectType.Type.WAIT_PUNCH.ordinal();
//                }
//                break;
//        }
//        results.add(typeA);
//        results.add(typeB);
//        connectResult.setResults(results);
//        return connectResult;
//    }


}
