package ppex.proto.entity.through;

/**
 * 因为是客户端和服务端共用.所以比较麻烦
 * 如果是Client端用,直接将CONNECT序列化放入ThroughTypeMsg即可
 * 如果是Server端用,需要将CONNECT序列化放入RECVINFO中,RECVINFO再放入ThroughTypeMsg.
 */
public class CONNECT {

    public enum TYPE{
        CONNECT_ERROR,
        REQUEST_CONNECT,            //一般是Client发给Server,请求与某个建立连接
        RECV_REQUEST_CONNECT,       //Server通过判断类型后返回Client,让该Client将包发到某个地址
        ;
    }

    private int type;
    private long from;
    private long to;
    private String content;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
