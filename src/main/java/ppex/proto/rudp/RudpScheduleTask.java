package ppex.proto.rudp;

import io.netty.util.Timeout;
import ppex.proto.tpool.ITask;
import ppex.proto.tpool.IThreadExecute;
import ppex.server.socket.Server;

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
        execute();
    }

    @Override
    public void run() {
        execute();
    }

    @Override
    public void execute() {
//        try {
//            long now = System.currentTimeMillis();
//            //超时,便是关闭连接
//            if (now - rudpPack.getTimeout() > rudpPack.getLasRcvTime()){
//                rudpPack.close();
//            }
//            if (!rudpPack.isActive()){
//                System.out.println("ScheduleTask rudp dead.not active " + rudpPack.getOutput().getConn().getAddress());
//                rudpPack.release();
//                Server.getInstance().getOutputManager().del(rudpPack.getOutput().getConn().getAddress());
//                addrManager.Del(rudpPack);
//                rudpPack = null;
//                return;
//            }
//            if (rudpPack.isStop()){
//                System.out.println("ScheduleTask rudp stop." + rudpPack.getOutput().getConn().getAddress());
//                rudpPack.release();
//                Server.getInstance().getOutputManager().del(rudpPack.getOutput().getConn().getAddress());
//                addrManager.Del(rudpPack);
//                rudpPack = null;
//                return;
//            }
//            //这个Next时间要看后面得到的时间长短来确定
////            System.out.println("Schedule task flush thread:" + Thread.currentThread().getName());
//            long next = rudpPack.flush(now,true);
//            executor.executeTimerTask(this,next);
//            if (!rudpPack.getQueue_snd().isEmpty() && rudpPack.canSend(false)){
//                rudpPack.notifySendEvent("RudpSchedule");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
