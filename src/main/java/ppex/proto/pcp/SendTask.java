package ppex.proto.pcp;

import io.netty.buffer.ByteBuf;
import io.netty.util.Recycler;
import org.apache.log4j.Logger;
import org.jctools.queues.MpscArrayQueue;
import ppex.utils.tpool.ITask;

public class SendTask implements ITask {

    private static Logger LOGGER = Logger.getLogger(SendTask.class);

    private final Recycler.Handle<SendTask> recyclerHandler;
    private static final Recycler<SendTask> RECYCLER = new Recycler<SendTask>() {
        @Override
        protected SendTask newObject(Handle<SendTask> handle) {
            return new SendTask(handle);
        }
    };

    private SendTask(Recycler.Handle<SendTask> recyclerHandler) {
        this.recyclerHandler = recyclerHandler;
    }

//    private PcpPack pcpPack;

    private Ukcp ukcp;
//    public static SendTask New(PcpPack pcpPack) {
//        SendTask sendTask = RECYCLER.get();
//        sendTask.pcpPack = pcpPack;
//        return sendTask;
//    }
    public static SendTask New(Ukcp ukcp){
        SendTask sendTask = RECYCLER.get();
        sendTask.ukcp = ukcp;
        return sendTask;
    }

    @Override
    public void execute() {
        try {

            MpscArrayQueue<ByteBuf> queue = ukcp.getSendList();
            while (ukcp.canSend(false)) {
                ByteBuf byteBuf = queue.poll();
                if (byteBuf == null)
                    break;
                try {
                    this.ukcp.send(byteBuf);
                    byteBuf.release();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (!ukcp.canSend(false) || (ukcp.checkFlush() && ukcp.isFastFlush())){   //当发现等待ack的队列数量比窗口的数量还多.执行发送动作
                LOGGER.info("SendTask cansend");
                long now = System.currentTimeMillis();
                long next = ukcp.flush(now);
                ukcp.setTsUpdate(now + next);
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            release();
        }
    }

    public void release() {
        ukcp = null;
        recyclerHandler.recycle(this);
    }
}
