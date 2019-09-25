package ppex.proto.type;

import ppex.utils.MessageUtil;

public class TypeMsg {
    private MessageUtil.MsgType type;
    private String body;

    public MessageUtil.MsgType getType() {
        return type;
    }

    public void setType(MessageUtil.MsgType type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "TypeMsg{" +
                "type=" + type +
                ", body='" + body + '\'' +
                '}';
    }
}
