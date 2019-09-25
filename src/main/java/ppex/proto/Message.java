package ppex.proto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import ppex.utils.Constants;

/**
 * -----16bits-----+-----16bits-----+-----32bits------+-----content-----+
 * --    0x01    --+--  msg type  --+--contentlength--+--   content   --+
 *-----------------+----------------+-----------------+-----------------+
 *
 * 2019-9-25 修改,将msg type放入content,解析content再获取type类型,为了后面udp实现realiable
 * -----8bits-----+-----32bits------+-----content-----+
 * --    0x01   --+--contentlength--+--   content   --+
 *----------------+-----------------+-----------------+
 *
 *
 *
 */

public class Message {
    public static final int VERSIONLENGTH = 1;
    public static final int CONTENTLENGTH = 4;

    public enum MsgType{
        MSG_TYPE_DETECT,
        MSG_TYPE_TXT,

    }
    private byte version;
    private int length;
    private String content;

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
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
                "version=" + Byte.toUnsignedInt(version) +
                ", length=" + length +
                ", content='" + content + '\'' +
                '}';
    }

    public static ByteBuf msg2ByteBuf(Message msg){
        ByteBuf msgBuf = Unpooled.directBuffer(msg.length + VERSIONLENGTH + CONTENTLENGTH + 1);
        msgBuf.writeByte(msg.getVersion());
        msgBuf.writeInt(msg.getLength());
        byte[] bytes = msg.getContent().getBytes(CharsetUtil.UTF_8);
        msgBuf.writeBytes(bytes);
        return msgBuf;
    }

    public static Message bytebuf2Msg(ByteBuf byteBuf){
        if (byteBuf.readableBytes() < (VERSIONLENGTH+CONTENTLENGTH)){
            return null;
        }
        byte version = byteBuf.readByte();
        if (version != Constants.MSG_VERION){
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
}
