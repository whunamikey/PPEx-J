package ppex.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import ppex.proto.Message;

public class MessageUtil {

    public static enum MsgType{
        MSG_TYPE_DETECT,
        MSG_TYPE_TXT,
    }

    public static ByteBuf msg2ByteBuf(Message msg){
        ByteBuf msgBuf = Unpooled.directBuffer(msg.getLength() + Message.VERSIONLENGTH + Message.CONTENTLENGTH + 1);
        msgBuf.writeByte(msg.getVersion());
        msgBuf.writeInt(msg.getLength());
        byte[] bytes = msg.getContent().getBytes(CharsetUtil.UTF_8);
        msgBuf.writeBytes(bytes);
        return msgBuf;
    }

    public static Message bytebuf2Msg(ByteBuf byteBuf){
        if (byteBuf.readableBytes() < (Message.VERSIONLENGTH+Message.CONTENTLENGTH)){
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