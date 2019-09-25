package ppex.server.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.log4j.Logger;
import ppex.utils.Constants;
import ppex.utils.Identity;

public class UdpServer {

    private Logger logger = Logger.getLogger(UdpServer.class);

    public void startUdpServer(int identity) {
        logger.info("---->UdpServer start");

        final EventLoopGroup workerGroup = new NioEventLoopGroup(3);
        final EventLoopGroup group = new NioEventLoopGroup(2);
        try {
            final Bootstrap peerBoostrap = new Bootstrap();
            peerBoostrap.group(workerGroup).channel(NioDatagramChannel.class)
                    .handler(new UdpServerHandler())
                    .option(ChannelOption.SO_BROADCAST, true);
            if (identity == Identity.Type.SERVER1.ordinal() || identity == Identity.Type.SERVER2_PORT1.ordinal()) {
                peerBoostrap.bind(Constants.PORT1).sync().channel().closeFuture().await();
            }else if (identity == Identity.Type.SERVER2_PORT2.ordinal()){
                peerBoostrap.bind(Constants.PORT2).sync().channel().closeFuture().await();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
