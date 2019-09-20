package ppex.socket.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ppex.utils.Constants;

public class UdpServer {

    private Channel channel;

    public void startUdpServer() {
        final EventLoopGroup workerGroup = new NioEventLoopGroup(3);
        final EventLoopGroup group = new NioEventLoopGroup(2);
        try {
            final Bootstrap peerBoostrap = new Bootstrap();
            peerBoostrap.group(workerGroup).channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel nioDatagramChannel) throws Exception {
                            ChannelPipeline pipeline = nioDatagramChannel.pipeline();
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(group, "handler", new UdpServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.SO_RCVBUF, 1024)
                    .option(ChannelOption.SO_SNDBUF, 1024);
            ChannelFuture future = peerBoostrap.bind(Constants.SERVER_PORT).sync();
            channel = future.channel();
            System.out.println("server start succ");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
