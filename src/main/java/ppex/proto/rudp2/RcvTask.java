package ppex.proto.rudp2;

import io.netty.buffer.ByteBuf;
import io.netty.util.Timeout;
import ppex.proto.msg.Message;
import ppex.proto.rudp.RudpPack;
import ppex.proto.tpool.ITask;

import java.util.concurrent.ConcurrentLinkedQueue;

public class RcvTask implements ITask {
    private RudpPack rpkg;
    private String name;

    public static RcvTask New(RudpPack rpkg, String name) {
        RcvTask rt = new RcvTask();
        rt.rpkg = rpkg;
        rt.name = name;
        return rt;
    }


    @Override
    public void execute() {
        try {
            ConcurrentLinkedQueue<ByteBuf> rcvList = rpkg.getQueue_rcv();
            long time = System.currentTimeMillis();
            while (!rcvList.isEmpty()) {
                ByteBuf buf = rcvList.poll();
                if (buf == null)
                    continue;
                rpkg.input2(buf,time);
                buf.release();
            }
            while(true){
                long msgid = rpkg.canRcv2();
                if (msgid == -1)
                    break;
                Message msg = rpkg.getMsg2(msgid);
                if (msg == null)
                    continue;
                rpkg.getListener().onResponse(rpkg,msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
