package ppex.server.myturn;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;

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

}
