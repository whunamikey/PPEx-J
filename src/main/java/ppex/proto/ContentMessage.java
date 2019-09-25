package ppex.proto;

public interface ContentMessage {
    default void handleContent(String content){
        System.out.println("handle content:" +content);
    }
}
