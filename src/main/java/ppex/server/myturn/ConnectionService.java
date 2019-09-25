package ppex.server.myturn;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ConnectionService {


    //server name ->connection
    private final Map<String,Connection> connections = new HashMap<>();

    public void addConnection(final Connection connection){
        final String peerName = connection.getPeerName();
        final Connection previousConnection = connections.put(peerName,connection);

        if (previousConnection != null){
            previousConnection.close();
            System.out.println("Already existing connection to " + peerName + " is closed.");
        }
    }

    public boolean removeConnection(final Connection connection){
        final boolean removed = connections.remove(connection.getPeerName()) != null;
        if (removed){
            System.out.println(connection.getPeerName() + " connection is removed from connections.");
        }else{
            System.out.println(connection.getPeerName() + " is not removed since not found in connections.");
        }
        return removed;
    }

    public int getNumberOfConnection(){
        return connections.size();
    }

    public boolean isConnectedTo(final String peerName){
        return connections.containsKey(peerName);
    }

    public Connection getConnection(final String peerName){
        return connections.get(peerName);
    }

    public Collection<Connection> getConnections(){
        return Collections.unmodifiableCollection(connections.values());
    }

    public void connectTo(String host, int port, CompletableFuture<Void> futureNotify){

    }

}
