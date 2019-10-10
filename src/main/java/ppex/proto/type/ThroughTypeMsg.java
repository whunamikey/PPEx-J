package ppex.proto.type;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 19-10-8 暂定
 * action 0 为保存connection信息,包括id,peername,nattype,ip address..content内容为id,peername,nattype,ipaddress
 * action 1 为获取所有connection信息,content格式为,from:1,to:2
 * action 2 为连接某个connection.即另一个peer了.content为需要连接的id,根据id找到connection
 * action 3 为返回信息的集合,content根据里面的分类再做分解
 */
public class ThroughTypeMsg {

    public enum ACTION {
        SAVE_INFO,
        GET_INFO,
        CONNECT,
        RECV_INFO,
    }

    //RECVTYPE为RECVINFO下面的type类型值,目前暂时有Id集合的JSON字符串,为GET_INFO的返回信息.RESPONSE是返回的一般信息反馈
    public enum RECVTYPE {
        SAVE_INFO,
        GET_INFO,
        CONNECT,
        RESPONSE,
    }

    private int action;
    private String content;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ThroughTypeMsg{" +
                "action=" + action +
                ", content='" + content + '\'' +
                '}';
    }
}