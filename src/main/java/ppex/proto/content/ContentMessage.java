package ppex.proto.content;

public interface ContentMessage {
    default void handleContent(String content){
        System.out.println("handle content:" +content);
    }
}
