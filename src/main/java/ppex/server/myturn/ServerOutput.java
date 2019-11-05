package ppex.server.myturn;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.msg.entity.Connection;
import ppex.proto.pcp.Pcp;
import ppex.proto.pcp.PcpOutput;
import ppex.proto.rudp.Output;
import ppex.proto.rudp.Rudp;

public class ServerOutput implements PcpOutput, Output {
    private Logger LOGGER = Logger.getLogger(ServerOutput.class);

    @Override
    public void out(ByteBuf data, Pcp pcp) {
        LOGGER.info("ServerOutput data");
        Connection connection = pcp.getConnection();
        DatagramPacket tmp = new DatagramPacket(data, connection.getAddress());
        connection.getChannel().writeAndFlush(tmp);
    }

    @Override
    public void output(ByteBuf data, Rudp rudp) {
        Connection connection = rudp.getConnection();
        DatagramPacket tmp = new DatagramPacket(data, connection.getAddress());
        connection.getChannel().writeAndFlush(tmp);
    }
}
