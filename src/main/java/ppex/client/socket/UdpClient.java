package ppex.client.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SocketUtils;
import ppex.client.entity.Client;
import ppex.proto.type.ProbeTypeMsg;
import ppex.proto.type.TypeMessage;
import ppex.utils.Constants;
import ppex.utils.Identity;
import ppex.utils.MessageUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class UdpClient {

    public void startClient(){

        Identity.INDENTITY = Identity.Type.CLIENT.ordinal();
        getLocalInetSocketAddress();

        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new UdpClientHandler());
            Channel ch = bootstrap.bind(Constants.PORT1).sync().channel();

            InetSocketAddress address = SocketUtils.socketAddress("127.0.0.1",9123);
            ProbeTypeMsg probeTypeMsg = new ProbeTypeMsg(TypeMessage.Type.MSG_TYPE_PROBE.ordinal(),address);
            ch.writeAndFlush(MessageUtil.probemsg2Packet(probeTypeMsg,Constants.SERVER_LOCAL_IP,Constants.PORT1));

            if (!ch.closeFuture().await(15000)){
                System.out.println("查询超时");
            }
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
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
