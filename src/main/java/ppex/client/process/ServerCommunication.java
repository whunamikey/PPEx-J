package ppex.client.process;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.log4j.Logger;
import ppex.client.socket.UdpClientHandler;
import ppex.utils.Constants;
import ppex.utils.Identity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ServerCommunication {
    private Logger LOGGER = Logger.getLogger(ServerCommunication.class);
    private static ServerCommunication instance = null;

    private ServerCommunication() {
    }

    public static ServerCommunication getInstance() {
        if (instance == null) {
            instance = new ServerCommunication();
        }
        return instance;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    private Queue<DatagramPacket> msgQueue = new ConcurrentLinkedDeque<>();
    private boolean stop = false;


    public void startCommunicationProcess() {
        new Thread(() -> {
            LOGGER.info("ServerCommunicationProcess thread running");
            EventLoopGroup group = new NioEventLoopGroup(1);
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group).channel(NioDatagramChannel.class)
                        .option(ChannelOption.SO_BROADCAST, true)
                        .handler(new UdpClientHandler());
                Channel channel = null;
                if (Identity.INDENTITY == Identity.Type.SERVER1.ordinal() || Identity.INDENTITY == Identity.Type.SERVER2_PORT1.ordinal()) {
                    channel = bootstrap.bind(Constants.PORT3).sync().channel();
                }else{
                    channel = bootstrap.bind(Constants.PORT4).sync().channel();
                }
                while (!stop) {
                    if (msgQueue.size() > 0) {
                        channel.writeAndFlush(msgQueue.poll());
                        if (!channel.closeFuture().await(15000)) {
                            System.out.println("查询超时");
                            continue;
                        }
                    } else {
//                        msgQueue.wait(1000);
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        }).start();
    }

    public void addMsg(DatagramPacket packet) {
        this.msgQueue.add(packet);
        this.msgQueue.notifyAll();
    }

}

