package ppex.client.socket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.msg.entity.Connection;
import ppex.proto.pcp.Pcp;
import ppex.proto.pcp.PcpOutput;

public class ClientOutput implements PcpOutput {
    private static Logger LOGGER = Logger.getLogger(ClientOutput.class);
    @Override
    public void out(ByteBuf data, Pcp pcp) {
        byte[] byteArray = new byte[data.capacity()];
        data.readBytes(byteArray);
        String result = new String(byteArray);
        data.resetReaderIndex();
        LOGGER.info("ClientOutput data:" + result);
        Connection connection = pcp.getConnection();
        DatagramPacket tmp = new DatagramPacket(data,connection.getAddress());
        connection.getChannel().writeAndFlush(tmp);
    }
}
