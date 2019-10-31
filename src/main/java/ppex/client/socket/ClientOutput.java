package ppex.client.socket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import ppex.proto.msg.entity.Connection;
import ppex.proto.pcp.Pcp;
import ppex.proto.pcp.PcpOutput;

public class ClientOutput implements PcpOutput {
    @Override
    public void out(ByteBuf data, Pcp pcp) {
        Connection connection = pcp.getConnection();
        DatagramPacket tmp = new DatagramPacket(data,connection.getAddress());
        connection.getChannel().writeAndFlush(tmp);
    }
}
