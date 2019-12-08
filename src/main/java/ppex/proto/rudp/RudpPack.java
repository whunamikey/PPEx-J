package ppex.proto.rudp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import ppex.proto.entity.Connection;
import ppex.proto.msg.Message;
import ppex.proto.tpool.IThreadExecute;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RudpPack {

    private final ConcurrentLinkedQueue<Message> queue_snd;
    private final ConcurrentLinkedQueue<ByteBuf> queue_rcv;

    private Rudp rudp;
    private IOutput output;
    private Connection connection;
    private IThreadExecute executor;
    private ResponseListener listener;

    private boolean isActive = true;
    private long lasRcvTime = System.currentTimeMillis(), timeout = 30 * 1000;

    public RudpPack(IOutput output, IThreadExecute executor, ResponseListener listener) {
        this.output = output;
        this.executor = executor;
        this.queue_snd = new ConcurrentLinkedQueue<>();
        this.queue_rcv = new ConcurrentLinkedQueue<>();
        this.rudp = new Rudp(output);
        this.listener = listener;
    }

    public boolean write(Message msg) {
        if (!queue_snd.offer(msg)) {
            return false;
        }
        notifySendEvent();
        return true;
    }

    public boolean send(Message msg) {
        return this.rudp.send(msg) == 0;
    }

    public void input(ByteBuf data, long time) {
        this.lasRcvTime = System.currentTimeMillis();
        this.rudp.input(data, time);

    }

    public void read(ByteBuf buf) {
        ByteBuf buf1 = PooledByteBufAllocator.DEFAULT.buffer(buf.readableBytes());
        buf1.writeBytes(buf);
        this.queue_rcv.add(buf1);
        notifyRcvEvent();
    }

    public void sendReset() {
        rudp.sendReset();
    }

    public void notifySendEvent() {
        SndTask task = SndTask.New(this);
        this.executor.execute(task);
    }

    public void notifyRcvEvent() {
        RcvTask task = RcvTask.New(this);
        this.executor.execute(task);
    }

    //暂时返回true
    public boolean canSend(boolean current) {
        int max = rudp.getWndSnd() * 2;
        int waitsnd = rudp.waitSnd();
        if (current) {
            return waitsnd < max;
        } else {
            int threshold = Math.max(1, max / 2);
            return waitsnd < threshold;
        }
    }


    public long flush(long current) {
        return rudp.flush(false, current);
    }

    public Queue<ByteBuf> getQueue_rcv() {
        return queue_rcv;
    }

    public int getInterval() {
        return rudp.getInterval();
    }

    public boolean canRcv() {
        return rudp.canRcv();
    }

    public ResponseListener getListener() {
        return listener;
    }

    public Message mergeRcv() {
        return rudp.mergeRcvData();
    }

    public void close() {
        this.isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public long getLasRcvTime() {
        return lasRcvTime;
    }

    public long getTimeout() {
        return timeout;
    }

    public void release() {
        rudp.release();
        queue_rcv.forEach(buf -> buf.release());
    }

    public ConcurrentLinkedQueue<Message> getQueue_snd() {
        return queue_snd;
    }

    public static RudpPack newInstance(IOutput output, IThreadExecute executor, ResponseListener responseListener) {
        return new RudpPack(output, executor, responseListener);
    }

}
