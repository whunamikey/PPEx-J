package ppex.proto.type;

public interface TypeMessageHandler {
    default void handleTypeMessage(TypeMessage msg){
        System.out.println("handleTypemsg:" + msg.toString());
    }
}
