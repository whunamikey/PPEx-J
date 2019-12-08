package ppex.server.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import ppex.proto.entity.Connection;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.IOutputManager;
import ppex.proto.rudp.ResponseListener;
import ppex.proto.tpool.IThreadExecute;
import ppex.proto.tpool.ThreadExecute;
import ppex.server.rudp.ServerAddrManager;
import ppex.server.rudp.ServerOutputManager;

import java.net.InetSocketAddress;

public class Server {

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


    public void startServer(){

        int cpunum = Runtime.getRuntime().availableProcessors();
        executor = new ThreadExecute();

        addrManager = new ServerAddrManager();
        executor = new ThreadExecute();
        executor.start();
        outputManager = new ServerOutputManager();
        serverHandler = new UdpServerHandler(this);
        responseListener = new MsgListener(addrManager);

        bootstrap = new Bootstrap();

    }

}
