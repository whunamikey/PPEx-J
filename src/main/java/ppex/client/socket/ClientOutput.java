package ppex.client.socket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import org.apache.log4j.Logger;
import ppex.proto.msg.entity.Connection;
import ppex.proto.pcp.Kcp;
import ppex.proto.pcp.PcpOutput;

public class ClientOutput implements PcpOutput {
    private static Logger LOGGER = Logger.getLogger(ClientOutput.class);
    @Override
    public void out(ByteBuf data, Kcp pcp) {
//        String result;
//        if (data.hasArray()){
//            result = new String(data.array(),data.arrayOffset()+data.readerIndex(),data.readableBytes());
//        }else{
//            byte[] bytes = new byte[data.readableBytes()];
//            data.getBytes(data.readerIndex(),bytes);
//            result = new String(bytes,0,data.readableBytes());
//        }
//        LOGGER.info("ClientOutput data:" + result + " readable:" + data.readableBytes());
        Connection connection = pcp.getConnection();
        DatagramPacket tmp = new DatagramPacket(data,connection.getAddress());
        connection.getChannel().writeAndFlush(tmp);
    }
}
