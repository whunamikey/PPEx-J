package ppex.proto.rudp2;

import io.netty.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.RudpPack;
import ppex.proto.tpool.ITask;
import ppex.proto.tpool.IThreadExecute;

public class ScheduleTask implements ITask {

    private static Logger LOGGER = LoggerFactory.getLogger(ScheduleTask.class);

    private IThreadExecute executor;
    private RudpPack rudpPack;
    private IAddrManager addrManager;

    public ScheduleTask(IThreadExecute executor, RudpPack rudpPack, IAddrManager addrManager) {
        this.executor = executor;
        this.rudpPack = rudpPack;
        this.addrManager = addrManager;
    }

    @Override
    public void execute() {
        try {
            long now = System.currentTimeMillis();
            if (now - rudpPack.getTimeout() > rudpPack.getLasRcvTime()) {
                rudpPack.close();
            }
            if (!rudpPack.isActive()) {
                rudpPack.release();
                addrManager.Del(rudpPack);
                rudpPack = null;
                return;
            }
            if (rudpPack.isStop2()) {
                rudpPack.release();
                addrManager.Del(rudpPack);
                rudpPack = null;
                return;
            }
            long nxt = rudpPack.flush2(now);
            executor.executeTimerTask(this, nxt);
            if (!rudpPack.getQueue_snd().isEmpty()) {
                rudpPack.notifySndTask2();
            }
            if (rudpPack.getRcvOrder().size() != 0 || rudpPack.getRcvShambles().size() != 0) {
                rudpPack.notifyRcvTask2();
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
