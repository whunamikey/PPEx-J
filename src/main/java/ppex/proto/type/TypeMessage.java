package ppex.proto.type;

import ppex.proto.Message;

public class TypeMessage {

    public static enum Type {
        MSG_TYPE_PROBE,
        MSG_TYPE_TXT,
    }

    private Type type;
    private String body;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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
        return "TypeMessage{" +
                "type=" + type +
                ", body='" + body + '\'' +
                '}';
    }
}
