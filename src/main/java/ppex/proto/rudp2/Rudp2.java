package ppex.proto.rudp2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppex.proto.msg.Message;
import ppex.proto.rudp.IOutput;
import ppex.utils.ByteUtil;
import ppex.utils.MessageUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 2019-12-23.暂不考虑其他重传算法以及RTT,RTO等时间计算.直接简单粗暴发送与接收
 * 2019-12-24.之前考虑的是有序到达,依靠sndNxt,rcvNxt,sndUna来确定有序到达的顺序.但是出现的问题是当一方断开之后,另一方不知道,还在继续发送.导致出现问题.
 * 所以考虑实现无序可靠到达.
 * 无序到达照样依靠sndNxt,rcvNxt,sndUna.sndNxt与之前一样,每一个就加1.sndUna就是加上每个消息的长度.可能就很大
 * 那么接收的sn顺序在rcvNxt于sndUna之间都可以接收.rcvNxt每一次.当rcvNxt==sndUna时是没有消息发送的
 */
public class Rudp2 {
    private static Logger LOGGER = LoggerFactory.getLogger(Rudp2.class);
    //发送数据与接收数据的集合
    private LinkedList<Chunk> sndList = new LinkedList<>();
    private LinkedList<Chunk> sndAckList = new LinkedList<>();
    //    private LinkedList<Chunk> rcvOrder = new LinkedList<>();
    private HashMap<Long, LinkedList<Chunk>> rcvMsgs = new HashMap<>();
    private LinkedList<Chunk> rcvShambles = new LinkedList<>();

    //多线程操作使用同步
    private Object sndLock = new Object();
    private Object sndAckLock = new Object();
    //    private Object rcvOrderLock = new Object();
    private Object rcvMsgsLock = new Object();
    private Object rcvShamebleLock = new Object();

    //数据长度
    private int mtuBody = RudpParam.MTU_BODY;
    //表示段的发送与接收的数值
    private int sndNxt, sndMax, sndMin, rcvMax, rcvMin;
    //超过发送次数就认为连接断开的值
    private int deadLink = RudpParam.DEAD_LINK;
    //rudp是否已经断开连接标志
    private boolean stop = false;
    private int rto = RudpParam.RTO_DEFAULT;
    private int interval = RudpParam.INTERVAL_DEFAULT;
    private int headLen = RudpParam.HEAD_LEN;

    private ByteBufAllocator byteBufAllocator = PooledByteBufAllocator.DEFAULT;


    //发送数据公共接口
    private IOutput output;

    public Rudp2(IOutput output) {
        this.output = output;
        sndNxt = 0;
        sndMax = 0;
        sndMin = 0;
        rcvMax = 0;
        rcvMin = 0;
    }

    //外面将msg放入Rudp的开始,可能有多个线程同时操作sndList
    public boolean snd(Message msg) {
        byte[] msgArr = MessageUtil.msg2Bytes(msg);
        byte[][] msgArrs = ByteUtil.splitArr(msgArr, mtuBody);
        synchronized (sndLock) {
            sndMax += msgArrs.length;
            for (int i = 0; i < msgArrs.length; i++) {
                Chunk chunk = Chunk.newChunk(msgArrs[i]);
                chunk.tot = i;
                chunk.sndMax = sndMax;
                chunk.all = msgArrs.length;
                chunk.msgid = msg.getMsgid();
                chunk.data = msgArrs[i];
                chunk.length = chunk.data.length;
                sndList.add(chunk);

            }
        }
        return true;
    }

    public void mvChkFromSnd2SndAck() {
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
        synchronized (sndAckLock) {
            sndAckList.addAll(tmpList);
        }
    }

    public long flush(long timeCur) {
        synchronized (sndAckLock) {
            sndAckList.forEach(chunk -> {
                boolean snd = false;
                if (chunk.xmit == 0) {
                    snd = true;
                    chunk.rto = rto;
                } else if (chunk.fastack >= 10) {
                    snd = true;
                } else if (timeDiff(chunk.ts_resnd, timeCur) >= 0) {
                    snd = true;
                    chunk.rto += rto;
                }
                if (snd) {
                    chunk.xmit++;
                    chunk.ts_resnd = timeCur + chunk.rto;
                    chunk.ts = timeCur;
                    if (chunk.xmit >= deadLink) {
                        stop = true;
                    }
                    ByteBuf buf = createOutputByteBuf(chunk);
                    sndChunk(buf, chunk.sn);
                }
            });
        }
        return interval;
    }

    public void rcv(ByteBuf buf, long time) {
        if (buf == null || buf.readableBytes() < headLen) {
            return;
        }
        while (true) {
            if (buf.readableBytes() <= 0) {
                break;
            }
            byte cmd = buf.readByte();
            long msgid = buf.readLong();
            int tot = buf.readInt();
            int all = buf.readInt();
            long ts = buf.readLong();
            long sn = buf.readLong();
            int sndMax = buf.readInt();
            int length = buf.readInt();
            if (buf.readableBytes() < length) {
                return;
            }
            if (cmd != RudpParam.CMD_SND && cmd != RudpParam.CMD_ACK) {
                return;
            }
            switch (cmd) {
                case RudpParam.CMD_SND:
                    byte[] data = new byte[length];
                    buf.readBytes(data, 0, length);
                    affirmSnd(msgid, tot, all, ts, sn, sndMax, length, data);
                    arrangeRcvShambles();
                    break;
                case RudpParam.CMD_ACK:
                    affirmAck(sn);
                    break;
            }
        }
    }


    private int timeDiff(long before, long after) {
        return (int) (after - before);
    }

    private ByteBuf createOutputByteBuf(Chunk chunk) {
        int length = headLen + chunk.length;
        ByteBuf buf = byteBufAllocator.ioBuffer(length);
        encodeBytebuf(buf, chunk);
        return buf;
    }

    private void encodeBytebuf(ByteBuf buf, Chunk chunk) {
        buf.writeByte(chunk.cmd);
        buf.writeLong(chunk.msgid);
        buf.writeInt(chunk.tot);
        buf.writeInt(chunk.all);
        buf.writeLong(chunk.ts);
        buf.writeLong(chunk.sn);
        buf.writeInt(chunk.sndMax);
        buf.writeInt(chunk.length);
        buf.writeBytes(chunk.data);
    }

    private void sndChunk(ByteBuf buf, long sn) {
        if (output != null) {
            output.output(buf, this, sn);
        } else {
            System.out.println("output is null");
        }
    }

    private void affirmAck(long sn) {
        synchronized (sndAckLock) {
            sndAckList.removeIf(chunk -> chunk.sn == sn);
        }
        if (!sndAckList.isEmpty()) {
            sndMin = (int) sndAckList.getFirst().sn;
        }
    }

    private void affirmSnd(long msgid, int tot, int all, long ts, long sn, int sndMax, int length, byte[] data) {
        Chunk chunk = Chunk.newChunk(data);
        chunk.msgid = msgid;
        chunk.tot = tot;
        chunk.all = all;
        chunk.ts = ts;
        chunk.sn = sn;
        chunk.sndMax = sndMax;
        chunk.length = length;
        rcvMax = Math.max(rcvMax, sndMax);
        if (sn >= rcvMin && sn < rcvMax) {
            boolean exist = rcvShambles.stream().anyMatch(chunk1 -> chunk1.sn == sn);
            if (!exist) {
                synchronized (rcvShamebleLock) {
                    rcvShambles.add(chunk);
                }
            }
        }
        flushAck(sn);
    }

    private void flushAck(long sn) {
        Chunk chunk = Chunk.newChunk(new byte[0]);
        chunk.cmd = RudpParam.CMD_ACK;
        chunk.sn = sn;
        ByteBuf buf = createOutputByteBuf(chunk);
        sndChunk(buf, sn);
    }

    private void arrangeRcvShambles() {
        LinkedList<Chunk> shambleCpy = new LinkedList<>();
        shambleCpy.addAll(rcvShambles);
        LinkedList<Chunk> delList = new LinkedList<>();
        LOGGER.info("shamblecpy size:" + shambleCpy.size() + " rcvShamble size:" + rcvShambles.size());
        synchronized (rcvMsgsLock) {
            shambleCpy.forEach(chunk -> {
                long msgid = chunk.msgid;
                boolean exist = rcvMsgs.containsKey(msgid);
                if (!exist) {
                    LinkedList<Chunk> chunks = new LinkedList<>();
                    for (int i = 0; i < chunk.all; i++) {
                        chunks.add(i, null);
                    }
                    rcvMsgs.put(msgid, chunks);
                }
                rcvMsgs.get(msgid).set(chunk.tot,chunk);
                delList.add(chunk);
            });
        }
        synchronized (rcvShambles) {
            rcvShambles.removeAll(delList);
        }
    }

    //返回可以提取的msgid
    public long canRcv() {
        AtomicLong msgid = new AtomicLong(-1);
        rcvMsgs.forEach((key, vals) -> {
            boolean notFull = vals.stream().anyMatch(chunk -> chunk == null);
            if (!notFull) {
                msgid.set(key);
                return;
            }
        });
        return msgid.get();
    }

    public Message mergeMsg(long msgid) {
        LinkedList<Chunk> chunks = rcvMsgs.get(msgid);
        int length = chunks.stream().mapToInt(chunk -> chunk.length).sum();
        byte[] result = new byte[length];
        for (int i = 0; i < chunks.size(); i++) {
            System.arraycopy(chunks.get(i).data, 0, result, mtuBody * i, chunks.get(i).data.length);
        }
        removeMsgsByMsgId(msgid);
        arrangeRcvShambles();
        Message msg = MessageUtil.bytes2Msg(result);
        return msg;
    }

    public void removeMsgsByMsgId(long msgid) {
        synchronized (rcvMsgsLock) {
            rcvMsgs.remove(msgid, rcvMsgs.get(msgid));
        }
    }

    public boolean isStop() {
        return stop;
    }

    public int getInterval() {
        return interval;
    }
}
