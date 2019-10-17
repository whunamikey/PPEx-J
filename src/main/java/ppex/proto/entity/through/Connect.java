package ppex.proto.entity.through;

/**
 * 因为是客户端和服务端共用.所以比较麻烦
 * 如果是Client端用,直接将CONNECT序列化放入ThroughTypeMsg即可
 * 如果是Server端用,需要将CONNECT序列化放入RECVINFO中,RECVINFO再放入ThroughTypeMsg.
 */
public class Connect {

    public enum TYPE{
        CONNECT_PING,                               //两边之间利用PING,PONG确认打通
        CONNECT_PONG,
        SOURCE_WAIT_TARGET_PUNCH,                   //打洞通信与反向打洞通信
        TARGET_ALREADY_PUNCH,                       //收到该消息后立马发送ping,看是否能从Target收到pong
        SOURCE_PUNCH_TARGET_WAIT,                   //等待ping消息到来,收到后,立马发送pong
        CONNECT_FORWARD_BUILD,                      //请求建立转发
        ;
    }

    private int type;
    //之前保留from和to.现在使用content保存connect包下的有关类的JSON字符串
    private String content;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Connect{" +
                "type=" + type +
                ", content='" + content + '\'' +
                '}';
    }
}
