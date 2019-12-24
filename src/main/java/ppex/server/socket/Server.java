package ppex.server.socket;

import io.netty.bootstrap.Bootstrap;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

@Component
public class Server {

    private static Logger LOGGER = Logger.getLogger(Server.class);

    @Autowired
    private ServerParam param;

    private String HOST_SERVER1 = "10.5.11.162";
    private String HOST_SERVER2 = "10.5.11.55";
    //    private String HOST_SERVER1 = "192.168.1.100";
//    private String HOST_SERVER2 = "192.168.1.102";
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

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private Server() {
    }

    public void startServer() throws Exception {
        initBootStrap();
    }

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
        boolean epoll = Epoll.isAvailable();
        if (epoll) {
            bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
        }
        eventLoopGroup = epoll ? new EpollEventLoopGroup(cpunum) : new NioEventLoopGroup(cpunum);
        Class<? extends Channel> chnCls = epoll ? EpollDatagramChannel.class : NioDatagramChannel.class;
        bootstrap.channel(chnCls).group(eventLoopGroup);
        bootstrap.option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_RCVBUF, Rudp.MTU_DEFUALT);

        bootstrap.handler(serverHandler);
    }

    private void initByType(Identity.Type type) throws Exception {
        LOGGER.info("Server initByType:" + type.ordinal());
        if (type == Identity.Type.SERVER1) {
            channel = bootstrap.bind(PORT_1).sync().channel();

            connServer2p1 = new Connection("Server2P1", addrServer2p1, "Server2P1", NatTypeUtil.NatType.UNKNOWN.getValue());
            IOutput output = new ServerOutput(channel, connServer2p1);
//            outputManager.put(addrServer2p1, output);
//            RudpPack rudpPack = new RudpPack(output, executor, responseListener);
//            addrManager.New(addrServer2p1, rudpPack);
            RudpPack rudpPack = RudpPack.newInstance(output, executor, responseListener, addrManager);
            addrManager.New(addrServer2p1, rudpPack);

            connServer2p2 = new Connection("Server2P2", addrServer2p2, "Server2P2", NatTypeUtil.NatType.UNKNOWN.getValue());
            IOutput output2p2 = new ServerOutput(channel, connServer2p2);
//            outputManager.put(addrServer2p2, output2p2);
//            RudpPack rudpPack2p2 = new RudpPack(output2p2, executor, responseListener);
//            addrManager.New(addrServer2p2, rudpPack2p2);
            RudpPack rudpPack2p2 = RudpPack.newInstance(output2p2, executor, responseListener, addrManager);
            addrManager.New(addrServer2p2, rudpPack2p2);

//            RudpScheduleTask scheduleTask = new RudpScheduleTask(executor, rudpPack, addrManager);
//            executor.executeTimerTask(scheduleTask, rudpPack.getInterval());

//            RudpScheduleTask scheduleTask2 = new RudpScheduleTask(executor, rudpPack2p2, addrManager);
//            executor.executeTimerTask(scheduleTask2, rudpPack2p2.getInterval());

        } else if (type == Identity.Type.SERVER2_PORT1) {
            channel = bootstrap.bind(PORT_1).sync().channel();

            connServer2p2 = new Connection("Server2P2", addrServer2p2, "Server2P2", NatTypeUtil.NatType.UNKNOWN.getValue());
            IOutput output = new ServerOutput(channel, connServer2p2);
//            outputManager.put(addrServer2p2, output);
//            RudpPack rudpPack = new RudpPack(output, executor, responseListener);
//            addrManager.New(addrServer2p2, rudpPack);
            RudpPack rudpPack = RudpPack.newInstance(output, executor, responseListener, addrManager);
            addrManager.New(addrServer2p2, rudpPack);

//            RudpScheduleTask scheduleTask2 = new RudpScheduleTask(executor, rudpPack, addrManager);
//            executor.executeTimerTask(scheduleTask2, rudpPack.getInterval());

        } else if (type == Identity.Type.SERVER2_PORT2) {
            channel = bootstrap.bind(PORT_2).sync().channel();
        }
    }

    private void stop() {
//        addrManager.getAll().forEach(rudpPack -> rudpPack.sendFinish());
        if (executor != null) {
            executor.stop();
            executor = null;
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    /**
     * ----------测试专用
     *
     * @return
     */
    public void startTestServer() {
        initParam();
        initBootStrap();
        try {
            channel = bootstrap.bind(PORT_1).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
