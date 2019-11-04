package ppex.proto.pcp;

import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.apache.log4j.Logger;
import ppex.utils.tpool.DisruptorExectorPool;
import ppex.utils.tpool.IMessageExecutor;
import ppex.utils.tpool.ITask;

public class ScheduleTask implements ITask, Runnable, TimerTask {
    private Logger LOGGER = Logger.getLogger(ScheduleTask.class);
    private IMessageExecutor executor;
//    private PcpPack pcpPack;
    private IChannelManager channelManager;
    private Ukcp ukcp;

    public ScheduleTask(IMessageExecutor executor, Ukcp ukcp, IChannelManager channelManager) {
        this.executor = executor;
        this.ukcp = ukcp;
        this.channelManager = channelManager;
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
//        LOGGER.info("ScheduleTask execute");
        try {
            //暂时没有判断是否存活.
            long now = System.currentTimeMillis();
            long next = ukcp.flush(now);
            //这个Next时间要看后面得到的时间长短来确定
            DisruptorExectorPool.scheduleHashedWheel(this, next);
            if (!ukcp.getSendList().isEmpty() && ukcp.canSend(false)) {
                ukcp.notifyWriteEvent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
