package ppex.client.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SocketUtils;
import org.apache.log4j.Logger;
import ppex.client.entity.Client;
import ppex.client.process.DetectProcess;
import ppex.client.process.ThroughProcess;
import ppex.utils.Constants;
import ppex.utils.Identity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class UdpClient {

    private static Logger LOGGER = Logger.getLogger(UdpClient.class);

    public void startClient(){

        Identity.INDENTITY = Identity.Type.CLIENT.ordinal();
        getLocalInetSocketAddress();

        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new UdpClientHandler());
            Channel ch = bootstrap.bind(Constants.PORT3).sync().channel();

            //1.探测阶段.开始DetectProcess
            DetectProcess.getInstance().setChannel(ch);
            DetectProcess.getInstance().startDetect();

            Client.getInstance().NAT_TYPE = DetectProcess.getInstance().getClientNATType().ordinal();
            System.out.println("Client NAT type is :" + Client.getInstance().NAT_TYPE);

            //2.穿越阶段
            ThroughProcess.getInstance().setChannel(ch);
            ThroughProcess.getInstance().sendSaveInfo();

//            while(true){
//                TimeUnit.SECONDS.sleep(1);
//                LOGGER.info("client while...");
//            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    private void getLocalInetSocketAddress(){
        Client.getInstance();
        try {
            InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
            String hostAddress = address.getHostAddress();//192.168.0.121
            Client.getInstance().local_address = address.getHostAddress();
            Client.getInstance().SERVER1 = SocketUtils.socketAddress(Constants.SERVER_HOST1,Constants.PORT1);
            Client.getInstance().SERVER2P1 = SocketUtils.socketAddress(Constants.SERVER_HOST2,Constants.PORT1);
            Client.getInstance().SERVER2P2 = SocketUtils.socketAddress(Constants.SERVER_HOST2,Constants.PORT2);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
