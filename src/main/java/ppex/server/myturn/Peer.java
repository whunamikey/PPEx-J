package ppex.server.myturn;

import io.netty.channel.Channel;

public class Peer {

    private final ConnectionService connectionService;
    private Channel bindChannel;
    private boolean running = true;

    public Peer(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    private boolean isShutdown(){
        return !running;
    }

    public void handleConnectionOpended(Connection connection,String leaderName){
        if (isShutdown()){
            System.out.println("new Connection of " + connection.getPeerName() + " ignored since not running.");
            return;
        }
        //todo here need to know how config work,need to add config after 9/24
        if (connection.getPeerName().equals("config")){
            connection.close();
            return;
        }
        connectionService.addConnection(connection);
        //todo need to add leaderService
        //todo need to add pingService to keepalive
    }


    public void handleConnectionClosed(Connection connection){
        if (connection == null)
            return;
        final String connectionPeerName = connection.getPeerName();
        //todo 这里的config是指代本身，代表本身自己这个节点
        if (connectionPeerName == null || connectionPeerName.equals("config")){
            return;
        }
        if (connectionService.removeConnection(connection)) {
            cancelPings(connection,connectionPeerName);
        }
        //todo cancel election leader peer in leadershipService
    }

    public void cancelPings(final Connection connection,final String peerName){
        if (running){
            //todo cancel pings,do cancel ping action in pingservice
        }
    }

    public void handlePing(Connection connection){
        if (running){
            //todo to ping action in pingservice
        }
    }

    public void keepalivePing(){
        if (isShutdown()){
            return;
        }
        if (connectionService.getNumberOfConnection() > 0){
            //todo do keepalive action in pingserivce
        }
    }


}
