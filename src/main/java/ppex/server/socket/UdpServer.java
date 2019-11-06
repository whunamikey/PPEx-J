package ppex.server.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SocketUtils;
import org.apache.log4j.Logger;
import ppex.proto.pcp.IChannelManager;
import ppex.proto.rudp.IAddrManager;
import ppex.server.entity.Server;
import ppex.server.myturn.ConnectionService;
import ppex.server.myturn.ServerAddrManager;
import ppex.server.myturn.ServerChannelManager;
import ppex.utils.Constants;
import ppex.utils.Identity;
import ppex.utils.tpool.DisruptorExectorPool;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

public class UdpServer {

    private Logger logger = Logger.getLogger(UdpServer.class);

    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private DisruptorExectorPool disruptorExectorPool;
    private List<Channel> channels = new Vector<>();
    private IChannelManager channelManager = ServerChannelManager.New();
    private IAddrManager addrManager = ServerAddrManager.getInstance();
    private UdpServerHandler udpServerHandler;

    public void startUdpServer(int identity) {

        initServer();

        logger.info("---->UdpServer start");
        bootstrap = new Bootstrap();
        int cpunum = Runtime.getRuntime().availableProcessors();

        //线程池初始化
        disruptorExectorPool = new DisruptorExectorPool();
        for (int i = 0;i < cpunum;i ++){
            disruptorExectorPool.createDisruptorProcessor("disruptor:" +i);
        }

        boolean epoll = Epoll.isAvailable();
        if (epoll) {
            bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
        }
        group = epoll ? new EpollEventLoopGroup(cpunum) : new NioEventLoopGroup(cpunum);
        Class<? extends Channel> channelCls = epoll ? EpollDatagramChannel.class : NioDatagramChannel.class;
        bootstrap.channel(channelCls);
        bootstrap.group(group);
        udpServerHandler = new UdpServerHandler(disruptorExectorPool,addrManager);
        bootstrap.handler(udpServerHandler);
        bootstrap.option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.SO_REUSEADDR, true);
//        for (int i =0;i < cpunum;i++){            //开启多个绑定
//
//        }
        if (identity == Identity.Type.SERVER1.ordinal() || identity == Identity.Type.SERVER2_PORT1.ordinal()) {
            ChannelFuture future = bootstrap.bind(Constants.PORT1);
            Channel channel = future.channel();
            channels.add(channel);
        } else if (identity == Identity.Type.SERVER2_PORT2.ordinal()) {
            ChannelFuture future = bootstrap.bind(Constants.PORT2);
            Channel channel = future.channel();
            channels.add(channel);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop()));
    }

    public void stop() {
        channels.forEach(channel -> channel.close());
        if (disruptorExectorPool != null)
            disruptorExectorPool.stop();
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    private void initServer() {
        try {
            logger.info("initServer run");
            Server.getInstance();
            InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
            String hostAddress = address.getHostAddress();//192.168.0.121
            Server.getInstance().local_address = address.getHostAddress();
//            Server.getInstance().SERVER1 = SocketUtils.socketAddress(Constants.SERVER_HOST1, Constants.PORT1);
//            Server.getInstance().SERVER2P1 = SocketUtils.socketAddress(Constants.SERVER_HOST2, Constants.PORT1);
//            Server.getInstance().SERVER2P2 = SocketUtils.socketAddress(Constants.SERVER_HOST2, Constants.PORT2);
            Server.getInstance().setSERVER1(SocketUtils.socketAddress(Constants.SERVER_HOST1, Constants.PORT1));
            Server.getInstance().setSERVER2P1(SocketUtils.socketAddress(Constants.SERVER_HOST2, Constants.PORT1));
            Server.getInstance().setSERVER2P2(SocketUtils.socketAddress(Constants.SERVER_HOST2, Constants.PORT2));

            ConnectionService.getInstance();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
