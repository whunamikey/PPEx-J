package ppex.socket.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import ppex.utils.Constants;

import java.net.InetSocketAddress;

public class UdpClient {

    public void startClient(){
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .option(ChannelOption.SO_RCVBUF,1024)
                    .option(ChannelOption.SO_SNDBUF,1024)
                    .handler(new UdpClientHandler());
            Channel ch = bootstrap.bind(Constants.CLIENT_PORT).sync().channel();
            ch.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("Client", CharsetUtil.UTF_8),
                    new InetSocketAddress("localhost",Constants.SERVER_PORT)
            )).sync();
            if (!ch.closeFuture().await(15000)){
                System.out.println("查询超时");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}
