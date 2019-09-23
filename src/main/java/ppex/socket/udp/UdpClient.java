package ppex.socket.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SocketUtils;
import ppex.proto.Message;
import ppex.utils.Constants;

public class UdpClient {

    public void startClient(){
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new UdpClientHandler());
            Channel ch = bootstrap.bind(0).sync().channel();

            Message msg = new Message();
            msg.setType(Short.valueOf(Message.MsgType.MSG_TYPE_TEXT.ordinal()+""));
            msg.setVersion(Constants.MSG_VERION);
            msg.setContent("msg from client");

            ch.writeAndFlush(new DatagramPacket(Message.msg2ByteBuf(msg), SocketUtils.socketAddress("127.0.0.1",Constants.SERVER_PORT))).sync();
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
