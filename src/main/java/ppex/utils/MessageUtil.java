package ppex.utils;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;
import ppex.proto.Message;
import ppex.proto.type.TypeMsg;

public class MessageUtil {

    public static enum MsgType {
        MSG_TYPE_PROBE,
        MSG_TYPE_TXT,
    }

    public static ByteBuf msg2ByteBuf(Message msg) {
        ByteBuf msgBuf = Unpooled.directBuffer(msg.getLength() + Message.VERSIONLENGTH + Message.CONTENTLENGTH + 1);
        msgBuf.writeByte(msg.getVersion());
        msgBuf.writeInt(msg.getLength());
        byte[] bytes = msg.getContent().getBytes(CharsetUtil.UTF_8);
        msgBuf.writeBytes(bytes);
        return msgBuf;
    }

    public static Message bytebuf2Msg(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < (Message.VERSIONLENGTH + Message.CONTENTLENGTH)) {
            return null;
        }
        byte version = byteBuf.readByte();
        if (version != Constants.MSG_VERSION) {
            return null;
        }
        int length = byteBuf.readInt();
        Message msg = new Message();
        msg.setVersion(version);
        msg.setLength(length);
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        String content = new String(bytes);
        msg.setContent(content);
        return msg;
    }

    public static DatagramPacket msg2Packet(Message message, String host, int port) {
        return new DatagramPacket(msg2ByteBuf(message), SocketUtils.socketAddress(host, port));
    }

    public static Message packet2Msg(DatagramPacket packet) {
        return bytebuf2Msg(packet.content());
    }

    public static DatagramPacket typemsg2Packet(TypeMsg typeMsg,String host,int port){
        Message msg = new Message();
        msg.setContent(typeMsg);
        return new DatagramPacket(msg2ByteBuf(msg),SocketUtils.socketAddress(host,port));
    }

    public static TypeMsg packet2Typemsg(DatagramPacket packet){
        Message msg = packet2Msg(packet);
        TypeMsg tMsg = JSON.parseObject(msg.getContent(),TypeMsg.class);
        return tMsg;
    }



}