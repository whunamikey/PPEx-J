package ppex.server.myturn;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.msg.entity.Connection;
import ppex.proto.pcp.Kcp;
import ppex.proto.pcp.PcpOutput;

public class ServerOutput implements PcpOutput {
    private Logger LOGGER = Logger.getLogger(ServerOutput.class);

    @Override
    public void out(ByteBuf data, Kcp pcp) {
        LOGGER.info("ServerOutput data");
        Connection connection = pcp.getConnection();
        DatagramPacket tmp = new DatagramPacket(data, connection.getAddress());
        connection.getChannel().writeAndFlush(tmp);
    }
}
