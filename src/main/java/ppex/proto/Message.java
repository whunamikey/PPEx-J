package ppex.proto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import ppex.utils.Constants;

/**
 * -----16bits-----+-----16bits-----+-----32bits-----+-----content-----+
 * --    0x01    --+--  msg type  --+-- msg length --+--   content   --+
 *-----------------+----------------+----------------+-----------------+
 *
 */

public class Message {
    public enum MsgType{
        MSG_TYPE_LOGIN,
        MSG_TYPE_LOGOUT,
        MSG_TYPE_LIST,
        MSG_TYPE_PUNCH,
        MSG_TYPE_PING,
        MSG_TYPE_PONG,
        MSG_TYPE_REPLY,
        MSG_TYPE_TEXT,
        MSG_TYPEEND
    }
    private short version;
    private short type;
    private int length;
    private String content;

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.length = content.getBytes(CharsetUtil.UTF_8).length;
    }

    @Override
    public String toString() {
        return "Message{" +
                "version=" + version +
                ", type=" + type +
                ", length=" + length +
                ", content='" + content + '\'' +
                '}';
    }

    public static ByteBuf msg2ByteBuf(Message msg){
        ByteBuf msgBuf = Unpooled.directBuffer(msg.length + 8+1);
        msgBuf.writeShort(msg.getVersion());
        msgBuf.writeShort(msg.getType());
        msgBuf.writeInt(msg.getLength());
        byte[] bytes = msg.getContent().getBytes(CharsetUtil.UTF_8);
        msgBuf.writeBytes(bytes);
        return msgBuf;
    }

    public static Message bytebuf2Msg(ByteBuf byteBuf){
        if (byteBuf.readableBytes() < 8){
            return null;
        }
        short version = byteBuf.readShort();
        if (version != Constants.MSG_VERION){
            return null;
        }
        short type = byteBuf.readShort();
        int length = byteBuf.readInt();
        Message msg = new Message();
        msg.setVersion(version);
        msg.setType(type);
        msg.setLength(length);
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        String content = new String(bytes);
        msg.setContent(content);
        return msg;
    }
}
