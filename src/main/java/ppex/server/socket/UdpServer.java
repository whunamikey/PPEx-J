package ppex.server.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.log4j.Logger;
import ppex.server.myturn.ConnectionService;
import ppex.server.myturn.Peer;
import ppex.utils.Constants;

public class UdpServer {

    private Logger logger = Logger.getLogger(UdpServer.class);

    private Channel channel;

    public void startUdpServer() {
        logger.info("---->UdpServer start");
        final EventLoopGroup workerGroup = new NioEventLoopGroup(3);
        final EventLoopGroup group = new NioEventLoopGroup(2);
        try {
            final Bootstrap peerBoostrap = new Bootstrap();
            peerBoostrap.group(workerGroup).channel(NioDatagramChannel.class)
                    .handler(new UdpServerHandler())
                    .option(ChannelOption.SO_BROADCAST, true);
            peerBoostrap.bind(Constants.SERVER_PORT1).sync().channel().closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
