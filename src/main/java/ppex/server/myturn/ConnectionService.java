package ppex.server.myturn;

import org.apache.log4j.Logger;
import ppex.proto.entity.Connection;
import ppex.proto.entity.through.Connect;
import ppex.proto.entity.through.ConnectMap;

import java.util.*;
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
    private Map<String,Connection> forwardSrcConnections = new HashMap<>(10,0.9f);
    private Map<String,Connection> forwardTgtConnections = new HashMap<>(10,0.9f);
    //正在连接的两个
    private List<ConnectMap> connectingList = new ArrayList<>();
    //已经建立连接的两个
    private List<ConnectMap> connectedList = new ArrayList<>();

    public boolean addConnection(final Connection connection) {
        final String peerName = connection.getPeerName();
        final Connection previousConnection = connections.put(connection.getMacAddress(), connection);
        if (previousConnection != null) {
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


    public boolean addConnecting(Connect.TYPE type, List<Connection> connections){
        return addConnecting(type.ordinal(),connections);
    }
    public boolean addConnecting(int type,List<Connection> connections){
        return connectingList.add(new ConnectMap(type,connections));
    }

    public boolean addConnected(int type,List<Connection> connections){
        return addConnected(new ConnectMap(type,connections));
    }

    public boolean addConnected(ConnectMap connectMap){
        addForwardConnection(connectMap);
        return connectedList.add(connectMap);
    }

    public boolean addForwardConnection(ConnectMap connectMap){
        if (connectMap.getConnectType() == Connect.TYPE.FORWARD.ordinal()){
//            forwardSrcConnections.put(connectMap.getConnections().get(0).macAddress,connectMap.getConnections().get(0));
//            forwardTgtConnections.put(connectMap.getConnections().get(1).macAddress,connectMap.getConnections().get(1));
            return true;
        }
        return false;
    }

}
