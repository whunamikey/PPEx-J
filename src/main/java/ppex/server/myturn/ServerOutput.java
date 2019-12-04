package ppex.server.myturn;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.msg.entity.Connection;
import ppex.proto.rudp.Output;
import ppex.proto.rudp.Rudp;

public class ServerOutput implements Output {
    private Logger LOGGER = Logger.getLogger(ServerOutput.class);

    @Override
    public void output(ByteBuf data, Rudp rudp,long sn) {
        Connection connection = rudp.getConnection();
        DatagramPacket tmp = new DatagramPacket(data, connection.getAddress());
        ChannelFuture future = connection.getChannel().writeAndFlush(tmp);
        future.addListener(fu -> {
            if (fu.isSuccess()){

            }else{
                LOGGER.info("Output unsuccess:" + fu.cause().toString());
                fu.cause().printStackTrace();
            }
        });
    }
}
