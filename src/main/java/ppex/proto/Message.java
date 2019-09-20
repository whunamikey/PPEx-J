package ppex.proto;

/**
 * -----16bits-----+-----16bits-----+-----32bits-----+-----content-----+
 * --    0x01    --+--  msg type  --+-- msg length --+--   content   --+
 *-----------------+----------------+----------------+-----------------+
 *
 */

public class Message {
    enum MsgType{
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
    }
}
