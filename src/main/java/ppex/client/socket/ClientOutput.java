package ppex.client.socket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.msg.entity.Connection;
import ppex.proto.pcp.Pcp;
import ppex.proto.pcp.PcpOutput;
import ppex.utils.MessageUtil;

public class ClientOutput implements PcpOutput {
    private static Logger LOGGER = Logger.getLogger(ClientOutput.class);
    @Override
    public void out(ByteBuf data, Pcp pcp) {
        Connection connection = pcp.getConnection();
        LOGGER.info("ClientOutput data:" + MessageUtil.bytebuf2Str(data));
        DatagramPacket tmp = new DatagramPacket(data,connection.getAddress());
        connection.getChannel().writeAndFlush(tmp);
    }
}
