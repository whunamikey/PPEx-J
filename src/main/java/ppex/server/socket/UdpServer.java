package ppex.server.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.log4j.Logger;
import ppex.proto.msg.entity.Connection;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.Output;
import ppex.proto.rudp.Rudp;
import ppex.proto.rudp.RudpPack;
import ppex.server.myturn.ConnectionService;
import ppex.server.myturn.ServerAddrManager;
import ppex.server.myturn.ServerOutput;
import ppex.utils.Constants;
import ppex.utils.Identity;
import ppex.utils.tpool.DisruptorExectorPool;
import ppex.utils.tpool.IMessageExecutor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class UdpServer {

    private Logger LOGGER = Logger.getLogger(UdpServer.class);

    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private DisruptorExectorPool disruptorExectorPool;
    private List<Channel> channels = new ArrayList<>();
    private IAddrManager addrManager = ServerAddrManager.getInstance();
    private UdpServerHandler udpServerHandler;
    private MsgListener msgListener;

    public void startUdpServer(int identity) {

        initServer();

        LOGGER.info("---->UdpServer start");
        bootstrap = new Bootstrap();
        int cpunum = Runtime.getRuntime().availableProcessors();

        //线程池初始化
        disruptorExectorPool = new DisruptorExectorPool();
        for (int i = 0; i < cpunum; i++) {
            disruptorExectorPool.createDisruptorProcessor("disruptor:" + i);
        }
        LOGGER.info("CPU num :" + cpunum);

        boolean epoll = Epoll.isAvailable();
        if (epoll) {
            bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
        }
        group = epoll ? new EpollEventLoopGroup(cpunum) : new NioEventLoopGroup(cpunum);
        Class<? extends Channel> channelCls = epoll ? EpollDatagramChannel.class : NioDatagramChannel.class;
        bootstrap.channel(channelCls);
        bootstrap.group(group);
        bootstrap.option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.SO_REUSEADDR, true)
            .option(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(Rudp.HEAD_LEN,Rudp.MTU_DEFUALT,Rudp.MTU_DEFUALT));

        msgListener = new MsgListener(addrManager);
        udpServerHandler = new UdpServerHandler(disruptorExectorPool, addrManager,msgListener);
        bootstrap.handler(udpServerHandler);
        if (identity == Identity.Type.SERVER1.ordinal()) {
            ChannelFuture future = bootstrap.bind(Constants.PORT1);
            Channel channel = future.channel();
            channels.add(channel);

            IMessageExecutor executor = disruptorExectorPool.getAutoDisruptorProcessor();
            Connection connection = new Connection("Server2P1", Server.getInstance().getSERVER2P1(), "Server2P1", 0, channel);
            Output output = new ServerOutput();
            RudpPack rudpPack = new RudpPack(output, connection, executor, msgListener, null);
            addrManager.New(Server.getInstance().getSERVER2P1(), rudpPack);

            IMessageExecutor executor2 = disruptorExectorPool.getAutoDisruptorProcessor();
            Connection connection2 = new Connection("Server2P2", Server.getInstance().getSERVER2P2(), "Server2P2", 0, channel);
            Output output2 = new ServerOutput();
            RudpPack rudpPack2 = new RudpPack(output2, connection2, executor2, msgListener, null);
            addrManager.New(Server.getInstance().getSERVER2P2(), rudpPack2);

        } else if (identity == Identity.Type.SERVER2_PORT1.ordinal()) {

            ChannelFuture future = bootstrap.bind(Constants.PORT1);
            Channel channel = future.channel();
            channels.add(channel);

            IMessageExecutor executor2 = disruptorExectorPool.getAutoDisruptorProcessor();
            Connection connection2 = new Connection("Server2P2", Server.getInstance().getSERVER2P2(), "Server2P2", 0, channel);
            Output output2 = new ServerOutput();
            RudpPack rudpPack2 = new RudpPack(output2, connection2, executor2, msgListener, null);
            addrManager.New(Server.getInstance().getSERVER2P2(), rudpPack2);

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
            LOGGER.info("initServer run");
            Server.getInstance();
            InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
            String hostAddress = address.getHostAddress();//192.168.0.121
            Server.getInstance().setLocal_address(address.getHostAddress());
            Server.getInstance().setSERVER1(new InetSocketAddress(Constants.SERVER_HOST1,Constants.PORT1));
            Server.getInstance().setSERVER2P1(new InetSocketAddress(Constants.SERVER_HOST2,Constants.PORT1));
            Server.getInstance().setSERVER2P2(new InetSocketAddress(Constants.SERVER_HOST2,Constants.PORT2));

            ConnectionService.getInstance();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
