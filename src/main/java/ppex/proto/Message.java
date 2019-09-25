package ppex.proto;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.bouncycastle.pqc.crypto.MessageSigner;
import ppex.proto.type.TypeMsg;
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
 */

public class Message {
    public static final int VERSIONLENGTH = 1;
    public static final int CONTENTLENGTH = 4;

    private byte version;
    private int length;
    private String content;

    public Message() {
        this.version = Constants.MSG_VERSION;
    }

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

    public void setContent(TypeMsg typeMsg){
        this.content = JSON.toJSONString(typeMsg);
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


}
