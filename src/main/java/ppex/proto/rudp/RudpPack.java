package ppex.proto.rudp;

import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.SpscArrayQueue;
import ppex.proto.msg.Message;
import ppex.proto.msg.entity.Connection;
import ppex.utils.tpool.IMessageExecutor;

import java.util.Queue;

public class RudpPack {

    private final MpscArrayQueue<Message> queue_snd;
    private final Queue<Message> queue_rcv;

    private Rudp rudp;
    private Output output;
    private Connection connection;
    private IMessageExecutor iMessageExecutor;

    public RudpPack(Output output, Connection connection, IMessageExecutor iMessageExecutor){
        this.output = output;
        this.connection = connection;
        this.iMessageExecutor = iMessageExecutor;
        this.queue_snd = new MpscArrayQueue<>(2 << 11);
        this.queue_rcv = new SpscArrayQueue<>(2 << 11);
        this.rudp = new Rudp(output,connection);
    }

    public boolean send(Message msg){
        return this.rudp.send(msg) == 0;
    }

    private void notifySendEvent(){

    }

    public MpscArrayQueue<Message> getQueue_snd() {
        return queue_snd;
    }
    //暂时返回true
    public boolean canSend(boolean current){
        return true;
    }

    public long flush(long current){
        return rudp.flush(false,current);
    }
}
