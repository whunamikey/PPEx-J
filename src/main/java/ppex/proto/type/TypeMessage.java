package ppex.proto.type;

public interface TypeMessage {
    default void handleTypeMessage(TypeMsg msg){
        System.out.println("handleTypemsg:" + msg.toString());
    }

    
}
