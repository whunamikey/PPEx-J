package ppex.proto.rudp2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import ppex.proto.msg.Message;
import ppex.proto.rudp.IOutput;
import ppex.proto.rudp.RudpPack;
import ppex.utils.ByteUtil;
import ppex.utils.MessageUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 2019-12-23.暂不考虑其他重传算法以及RTT,RTO等时间计算.直接简单粗暴发送与接收
 */
public class Rudp2 {

    private ByteBufAllocator byteBufAllocator = PooledByteBufAllocator.DEFAULT;

    //发送数据与接收数据的集合
    private LinkedList<Chunk> sndList = new LinkedList<>();
    private LinkedList<Chunk> sndAckList = new LinkedList<>();
    private LinkedList<Chunk> rcvOrder = new LinkedList<>();
    private LinkedList<Chunk> rcvShambles = new LinkedList<>();

    //多线程操作使用同步
    private Object sndLock = new Object();
    private Object sndAckLock = new Object();
    private Object rcvOrderLock = new Object();
    private Object rcvShamebleLock = new Object();

    //数据长度
    private int mtuBody = RudpParam.MTU_BODY;
    //表示段的发送与接收的数值
    private int sndNxt, rcvNxt, sndUna;
    //处理窗口值
    private int wnd_snd = RudpParam.WND_SND;
    private int wnd_rcv = RudpParam.WND_RCV;
    //超过发送次数就认为连接断开的值
    private int deadLink = RudpParam.DEAD_LINK;
    //rudp是否已经断开连接标志
    private boolean stop = false;
    private int rto = RudpParam.RTO_DEFAULT;


    //发送数据公共接口
    private IOutput output;

    public Rudp2(IOutput output) {
        this.output = output;
        sndNxt = 0;
        sndUna = 0;
        rcvNxt = 0;
    }

    //外面将msg放入Rudp的开始,可能有多个线程同时操作sndList
    public boolean send(Message msg) {
        byte[] msgArr = MessageUtil.msg2Bytes(msg);
        byte[][] msgArrs = ByteUtil.splitArr(msgArr, mtuBody);
        synchronized (sndLock) {
            for (int i = 0; i < msgArrs.length; i++) {
                Chunk chunk = Chunk.newChunk(msgArrs[i]);
                sndList.add(chunk);
            }
        }
        return true;
    }

    private void mvChkFromSnd2SndAck() {
        LinkedList<Chunk> tmpList = new LinkedList<>();
        synchronized (sndLock) {
            while (!sndList.isEmpty()) {
                Chunk chunk = sndList.removeFirst();
                chunk.cmd = RudpParam.CMD_SND;
                chunk.sn = sndNxt;
                sndNxt++;
                tmpList.add(chunk);
            }
        }
        synchronized (sndAckLock){
            sndAckList.addAll(tmpList);
        }
    }

    public long flush(long timeCur){

        sndAckList.forEach(chunk -> {
            boolean snd = false;
            if (chunk.xmit == 0){
                snd = true;
                chunk.ts_resnd = timeCur+rto;
            }else if (timeDiff(chunk.ts_resnd,timeCur) >= 0){
                snd = true;
                chunk.ts_resnd = timeCur + rto;
            }
            if (snd){
                chunk.xmit++;
                if (chunk.xmit > deadLink){
                    stop = true;
                }
                chunk.ts = timeCur;
                chunk.una = rcvNxt;
            }
        });
        
        return 0;
    }

    private int timeDiff(long before,long after){
        return (int)(after - before);
    }

}
