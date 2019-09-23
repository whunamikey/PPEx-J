package ppex.socket.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import ppex.utils.Constants;

public class UdpServer {

    private Channel channel;

    public void startUdpServer() {
        final EventLoopGroup workerGroup = new NioEventLoopGroup(3);
        final EventLoopGroup group = new NioEventLoopGroup(2);
        try {
            final Bootstrap peerBoostrap = new Bootstrap();
            peerBoostrap.group(workerGroup).channel(NioDatagramChannel.class)
                    .handler(new UdpServerHandler())
                    .option(ChannelOption.SO_BROADCAST, true);
            peerBoostrap.bind(Constants.SERVER_PORT).sync().channel().closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
