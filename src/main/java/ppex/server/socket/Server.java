package ppex.server.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.log4j.Logger;
import ppex.proto.entity.Connection;
import ppex.proto.rudp.*;
import ppex.proto.tpool.IThreadExecute;
import ppex.proto.tpool.ThreadExecute;
import ppex.server.rudp.ServerAddrManager;
import ppex.server.rudp.ServerOutput;
import ppex.server.rudp.ServerOutputManager;
import ppex.utils.Identity;
import ppex.utils.NatTypeUtil;

import java.net.InetSocketAddress;

public class Server {

    private static Logger LOGGER = Logger.getLogger(Server.class);

    private String HOST_SERVER1 = "10.5.11.55";
    private String HOST_SERVER2 = "127.0.0.1";
    private int PORT_1 = 9123;
    private int PORT_2 = 9124;
    private int PORT_3 = 9125;

    private InetSocketAddress addrLocal;
    private InetSocketAddress addrServer1;
    private InetSocketAddress addrServer2p1;
    private InetSocketAddress addrServer2p2;


    private String addrMac;
    private String name;
    private Connection connServer1;
    private Connection connServer2p1;
    private Connection connServer2p2;
    private Connection connLocal;

    private IAddrManager addrManager;
    private IOutputManager outputManager;
    private IThreadExecute executor;
    private ResponseListener responseListener;

    private Channel channel;
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;
    private UdpServerHandler serverHandler;

    public static Server instance = null;
    public static Server getInstance(){
        if (instance == null){
            instance = new Server();
        }
        return instance;
    }
    private Server(){}

    public void startServer(Identity.Type type) throws Exception {
        initParam();
        initBootStrap();
        initByType(type);
    }

    private void initParam() {

        addrServer1 = new InetSocketAddress(HOST_SERVER1, PORT_1);
        addrServer2p1 = new InetSocketAddress(HOST_SERVER2, PORT_1);
        addrServer2p2 = new InetSocketAddress(HOST_SERVER2, PORT_2);

        connLocal = new Connection(name, addrLocal, name, NatTypeUtil.NatType.UNKNOWN.getValue());

        executor = new ThreadExecute();
        addrManager = new ServerAddrManager();
        executor = new ThreadExecute();
        executor.start();
        outputManager = new ServerOutputManager();
        serverHandler = new UdpServerHandler(getInstance());
        responseListener = new MsgListener(addrManager);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop()));
    }

    private void initBootStrap() {
        int cpunum = Runtime.getRuntime().availableProcessors();
        bootstrap = new Bootstrap();
        //todo 2019.12.8
        boolean epoll = Epoll.isAvailable();
        if (epoll) {
            bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
        }
        eventLoopGroup = epoll ? new EpollEventLoopGroup(cpunum) : new NioEventLoopGroup(cpunum);
        Class<? extends Channel> chnCls = epoll ? EpollDatagramChannel.class : NioDatagramChannel.class;
        bootstrap.channel(chnCls).group(eventLoopGroup);
        bootstrap.option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(Rudp.HEAD_LEN, Rudp.MTU_DEFUALT, Rudp.MTU_DEFUALT));

        bootstrap.handler(serverHandler);
    }

    private void initByType(Identity.Type type) throws Exception {
        LOGGER.info("Server initByType:" + type.ordinal());
        if (type == Identity.Type.SERVER1) {
            channel = bootstrap.bind(PORT_1).sync().channel();

            connServer2p1 = new Connection("Server2P1", addrServer2p1, "Server2P1", NatTypeUtil.NatType.UNKNOWN.getValue());
            IOutput output = new ServerOutput(channel, connServer2p1);
            outputManager.put(addrServer2p1, output);
            RudpPack rudpPack = new RudpPack(output, executor, responseListener);
            addrManager.New(addrServer2p1, rudpPack);

            connServer2p2 = new Connection("Server2P2", addrServer2p2, "Server2P2", NatTypeUtil.NatType.UNKNOWN.getValue());
            IOutput output2p2 = new ServerOutput(channel, connServer2p2);
            outputManager.put(addrServer2p2, output2p2);
            RudpPack rudpPack2p2 = new RudpPack(output2p2, executor, responseListener);
            addrManager.New(addrServer2p2, rudpPack2p2);
        } else if (type == Identity.Type.SERVER2_PORT1) {
            channel = bootstrap.bind(PORT_1).sync().channel();

            connServer2p2 = new Connection("Server2P2", addrServer2p2, "Server2P2", NatTypeUtil.NatType.UNKNOWN.getValue());
            IOutput output = new ServerOutput(channel, connServer2p2);
            outputManager.put(addrServer2p2, output);
            RudpPack rudpPack = new RudpPack(output, executor, responseListener);
            addrManager.New(addrServer2p2, rudpPack);
        } else if (type == Identity.Type.SERVER2_PORT2) {
            channel = bootstrap.bind(PORT_2).sync().channel();
        }
    }

    private void stop() {
        if (executor != null) {
            executor.stop();
            executor = null;
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public IAddrManager getAddrManager() {
        return addrManager;
    }

    public IOutputManager getOutputManager() {
        return outputManager;
    }

    public ResponseListener getResponseListener() {
        return responseListener;
    }

    public IThreadExecute getExecutor() {
        return executor;
    }

    public InetSocketAddress getAddrServer1() {
        return addrServer1;
    }

    public InetSocketAddress getAddrServer2p1() {
        return addrServer2p1;
    }

    public InetSocketAddress getAddrServer2p2() {
        return addrServer2p2;
    }
}
