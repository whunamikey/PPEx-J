package ppex.test;

import org.junit.Test;
import ppex.proto.msg.Message;
import ppex.utils.MessageUtil;

public class MessageTest {

    @Test
    public void MessageSeriableTest(){
        System.out.println("this is start");
        Message msg = new Message(12L);
        msg.setContent("this is msg");
        System.out.println("version:" +msg.getVersion()+"msgid:" + msg.getMsgid() + " contentlen:" + msg.getLength() + " content:" + msg.getContent());
        byte[] result = MessageUtil.msg2Bytes(msg);
        Message after = MessageUtil.bytes2Msg(result);
        System.out.println("version:" +after.getVersion()+"msgid:" + after.getMsgid() + " contentlen:" + after.getLength() + " content:" + after.getContent());
    }
}
