package ppex.proto.rudp;

import io.netty.util.Timeout;
import ppex.proto.tpool.ITask;
import ppex.proto.tpool.IThreadExecute;

public class RudpScheduleTask implements ITask {
    private IThreadExecute executor;
    private RudpPack rudpPack;
    private IAddrManager addrManager;

    public RudpScheduleTask(IThreadExecute executor, RudpPack rudpPack, IAddrManager addrManager) {
        this.executor = executor;
        this.rudpPack = rudpPack;
        this.addrManager = addrManager;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        run();
    }

    @Override
    public void run() {
        this.executor.execute(this);
    }

    @Override
    public void execute() {
        try {
            System.out.println("RudpScheduleTask execute");
            long now = System.currentTimeMillis();
            //超时,便是关闭连接
            if (now - rudpPack.getTimeout() > rudpPack.getLasRcvTime()){
                rudpPack.close();
            }
            if (!rudpPack.isActive()){
                rudpPack.release();
                return;
            }
            //这个Next时间要看后面得到的时间长短来确定
            long next = rudpPack.flush(now);
            executor.executeTimerTask(this,next);
            if (!rudpPack.getQueue_snd().isEmpty() && rudpPack.canSend(false)){
                rudpPack.notifySendEvent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
