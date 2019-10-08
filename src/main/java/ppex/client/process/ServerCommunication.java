package ppex.client.process;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import ppex.client.socket.UdpClientHandler;
import ppex.utils.Constants;

public class ServerCommunication {
    private static ServerCommunication instance = null;

    private ServerCommunication() {
    }

    public static ServerCommunication getInstance() {
        if (instance == null) {
            instance = new ServerCommunication();
        }
        return instance;
    }

    public void startCommunicationProcess(DatagramPacket packet) {
        new Thread(() -> {
            EventLoopGroup group = new NioEventLoopGroup(1);
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group).channel(NioDatagramChannel.class)
                        .option(ChannelOption.SO_BROADCAST, true)
                        .handler(new UdpClientHandler());
                Channel ch = bootstrap.bind(Constants.PORT3).sync().channel();
                ch.writeAndFlush(packet);
                if (!ch.closeFuture().await(15000)) {
                    System.out.println("查询超时");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        }).start();
    }

}

