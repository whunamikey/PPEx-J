package ppex.proto.rudp;

import io.netty.util.Timeout;
import ppex.proto.msg.Message;
import ppex.proto.tpool.ITask;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SndTask implements ITask {

    private RudpPack rudpkg;
    private String name;

    public static SndTask New(RudpPack rudpkg,String name) {
        SndTask sendTask = new SndTask();
        sendTask.rudpkg = rudpkg;
        sendTask.name = name;
        return sendTask;
    }

    @Override
    public void execute() {
        try {
            ConcurrentLinkedQueue<Message> msgs = rudpkg.getQueue_snd();
            while(rudpkg.canSend(false)){
                Message msg = msgs.poll();
                if (msg == null)
                    break;
                this.rudpkg.send(msg);
            }
            long cur = System.currentTimeMillis();
            System.out.println("sntask hash:" + this.hashCode() + " thread:" + Thread.currentThread().getName() + " rudp:" + rudpkg.getRudp().hashCode() + " toaddr:" + rudpkg.getOutput().getConn().getAddress() + " name:" + name);
            this.rudpkg.flush(cur,false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release();
        }
    }

    public void release() {
        rudpkg = null;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        run();
    }

    @Override
    public void run() {
        execute();
    }
}
