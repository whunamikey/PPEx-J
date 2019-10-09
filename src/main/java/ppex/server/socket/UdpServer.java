package ppex.server.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SocketUtils;
import org.apache.log4j.Logger;
import ppex.client.process.ServerCommunication;
import ppex.server.entity.Server;
import ppex.utils.Constants;
import ppex.utils.Identity;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UdpServer {

    private Logger logger = Logger.getLogger(UdpServer.class);

    public void startUdpServer(int identity) {

        initServer();

//        ServerCommunication.getInstance().startCommunicationProcess();

        logger.info("---->UdpServer start");
        final EventLoopGroup workerGroup = new NioEventLoopGroup(3);
        final EventLoopGroup group = new NioEventLoopGroup(2);
        try {
            final Bootstrap peerBoostrap = new Bootstrap();
            peerBoostrap.group(workerGroup).channel(NioDatagramChannel.class)
                    .handler(new UdpServerHandler())
                    .option(ChannelOption.SO_BROADCAST, true);
            Channel channel = null;
            if (identity == Identity.Type.SERVER1.ordinal() || identity == Identity.Type.SERVER2_PORT1.ordinal()) {
//                peerBoostrap.bind(Constants.PORT1).sync().channel().closeFuture().await();
                channel = peerBoostrap.bind(Constants.PORT1).sync().channel();
            } else if (identity == Identity.Type.SERVER2_PORT2.ordinal()) {
//                peerBoostrap.bind(Constants.PORT2).sync().channel().closeFuture().await();
                channel = peerBoostrap.bind(Constants.PORT2).sync().channel();
            }
            channel.closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    private void initServer() {
        try {
            Server.getInstance();
            InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
            String hostAddress = address.getHostAddress();//192.168.0.121
            Server.getInstance().local_address = address.getHostAddress();
            Server.getInstance().SERVER1 = SocketUtils.socketAddress(Constants.SERVER_HOST1, Constants.PORT1);
            Server.getInstance().SERVER2P1 = SocketUtils.socketAddress(Constants.SERVER_HOST2, Constants.PORT1);
            Server.getInstance().SERVER2P2 = SocketUtils.socketAddress(Constants.SERVER_HOST2, Constants.PORT2);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
