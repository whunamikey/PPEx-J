package ppex.server.rudp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramPacket;
import ppex.proto.entity.Connection;
import ppex.proto.rudp.IOutput;
import ppex.proto.rudp.Rudp;

public class ServerOutput implements IOutput {


    private Channel channel;
    private Connection connection;

    public ServerOutput(Channel channel, Connection connection) {
        this.channel = channel;
        this.connection = connection;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void update(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void output(ByteBuf data, Rudp rudp, long sn) {
        DatagramPacket packet = new DatagramPacket(data, connection.getAddress());
        if (channel.isActive() && channel.isOpen()) {
            ChannelFuture fu = channel.writeAndFlush(packet);
            fu.addListener(future -> {
                if (future.isSuccess()) {
//                    System.out.println("channel writeandflush succ" + sn);
                } else {
                    System.out.println("channel writeandflush failed");
                    future.cause().printStackTrace();
                }
            });
        } else {
            System.out.println("channel is close");
        }
    }

    @Override
    public Connection getConn() {
        return this.connection;
    }

}
