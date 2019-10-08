package ppex.client.process;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;

public class ServerCommunication {
    private static ServerCommunication instance = null;
    private ServerCommunication(){
    }
    public static ServerCommunication getInstance(){
        if (instance == null){
            instance = new ServerCommunication();
        }
        return instance;
    }

    private Channel channel;
    public void sendMsg2S2P2(DatagramPacket packet){
        channel.writeAndFlush(packet);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}

