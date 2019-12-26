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

import java.util.Iterator;
import java.util.LinkedList;

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
    private LinkedList<Chunk> rcvOrder = new LinkedList<>();
    private LinkedList<Chunk> rcvShambles = new LinkedList<>();
    private LinkedList<Chunk> oldDataList = new LinkedList<>();

    //多线程操作使用同步
    private Object sndLock = new Object();
    private boolean sndWait = false;
    private Object sndAckLock = new Object();
    private boolean sndAckWait = false;
    private Object rcvOrderLock = new Object();
    private boolean rcvOrderWait = false;
    private Object rcvShamebleLock = new Object();
    private boolean rcvShambleWait = false;
    private Object oldDataLock = new Object();
    private boolean oldDataWait = false;

    //数据长度
    private int mtuBody = RudpParam.MTU_BODY;
    //表示段的发送与接收的数值,sndNxt下一次发送的sn号,rcvNxt下一次应该接受的sn号,sndUna未确认的sn号(就是发送了N过过去之后,依然还有最开始的未确认的sn号)
    private int sndNxt, rcvNxt, sndUna;
    //超过发送次数就认为连接断开的值
    private int deadLink = RudpParam.DEAD_LINK;
    //rudp是否已经断开连接标志
    private boolean stop = false;
    private int rto = RudpParam.RTO_DEFAULT;
    private int interval = RudpParam.INTERVAL_DEFAULT;
    private int headLen = RudpParam.HEAD_LEN;

    private ByteBufAllocator byteBufAllocator = PooledByteBufAllocator.DEFAULT;

    //添加该标识位为了解决断开重连后消息处理问题
    private byte tag = RudpParam.TAG_NEW;

    //发送数据公共接口
    private IOutput output;

    public Rudp2(IOutput output) {
        this.output = output;
        sndNxt = 0;
        rcvNxt = 0;
        sndUna = 0;
        tag = RudpParam.TAG_NEW;
        LOGGER.info("sndLock:" + Integer.toHexString(System.identityHashCode(sndLock)) + " sndackLock:" + Integer.toHexString(System.identityHashCode(sndAckLock)) +
                " rcvorderLock:" + Integer.toHexString(System.identityHashCode(rcvOrderLock)) + " rcvShamblesLock:" + Integer.toHexString(System.identityHashCode(rcvShamebleLock)));
    }

    //外面将msg放入Rudp的开始,可能有多个线程同时操作sndList
    public boolean snd(Message msg) {
        byte[] msgArr = MessageUtil.msg2Bytes(msg);
        byte[][] msgArrs = ByteUtil.splitArr(msgArr, mtuBody);
        synchronized (sndLock) {
            try {
                while (sndWait) {
                    sndLock.wait();
                }
                sndWait = true;
                for (int i = 0; i < msgArrs.length; i++) {
                    Chunk chunk = Chunk.newChunk(msgArrs[i]);
                    chunk.tot = i;
                    chunk.una = sndUna;
                    chunk.all = msgArrs.length;
                    chunk.msgid = msg.getMsgid();
                    chunk.length = chunk.data.length;
                    sndList.add(chunk);
                }
            } catch (Exception e) {
            } finally {
                sndWait = false;
                sndLock.notifyAll();
            }

        }
        return true;
    }

    public void mvChkFromSnd2SndAck() {
        LinkedList<Chunk> tmpList = new LinkedList<>();
        synchronized (sndLock) {
            try {
                while (sndWait) {
                    sndLock.wait();
                }
                sndWait = true;
                while (!sndList.isEmpty()) {
                    Chunk chunk = sndList.removeFirst();
                    chunk.cmd = RudpParam.CMD_SND;
                    chunk.sn = sndNxt;
                    sndNxt++;
                    tmpList.add(chunk);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sndWait = false;
                sndLock.notifyAll();
            }
        }
        synchronized (sndAckLock) {
            try {
                while (sndAckWait) {
                    sndAckLock.wait();
                }
                sndAckWait = true;
                sndAckList.addAll(tmpList);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sndAckWait = false;
                sndAckLock.notifyAll();
            }
        }
    }

    public long flush(long timeCur) {
        synchronized (sndAckLock) {
            try {
                while (sndAckWait) {
                    sndAckLock.wait();
                }
                sndAckWait = true;

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
                return interval;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sndAckWait = false;
                sndAckLock.notifyAll();
            }
        }
        return interval;
    }

    public void rcv(ByteBuf buf, long time) {
//        LOGGER.info("rudp2 rcv :" + " order size:" + rcvOrder.size() + " sb size:" + rcvShambles.size());
        if (buf == null || buf.readableBytes() < headLen) {
            return;
        }
        while (true) {
            if (buf.readableBytes() <= 0) {
                break;
            }
            byte tag = buf.readByte();
            byte cmd = buf.readByte();
            long msgid = buf.readLong();
            int tot = buf.readInt();
            int all = buf.readInt();
            long ts = buf.readLong();
            int sn = buf.readInt();
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
//                    dealTag(sn, tag);
                    //当sn == 0.己方为new而且对方消息也为new时，只需将自己的tag设为old
                    //当己方为old时且对方消息为new时,这时候应该先将队列中所有的消息清除（不需要发送给对方。因为对方是新的。只需设置自己的sndNxt,sndUna,rcvNxt）
                    //当己方为new时且对方消息为old时,不需要处理数据,发送sn为0的new数据。
                    if (sn == 0) {
                        if (this.tag == RudpParam.TAG_NEW && tag == RudpParam.TAG_NEW) {
                            this.tag = RudpParam.TAG_OLD;
                        }
                        if (this.tag == RudpParam.TAG_OLD && tag == RudpParam.TAG_NEW) {
                            sndNxt = 0;
                            sndUna = 0;
                            rcvNxt = 0;
                            //清除所有发送数据
                            sndList.clear();
                            sndAckList.clear();
                        }
                    } else {
                        if (this.tag == RudpParam.TAG_NEW && tag == RudpParam.TAG_OLD) {
                            Message msg = new Message(-1);
                            msg.setContent("");
                            snd(msg);
                            flushAck(sn);
                            break;
                        }
                    }
                    affirmSnd(tag, msgid, tot, all, ts, sn, sndMax, length, data);
                    arrangeRcvShambles();
                    break;
                case RudpParam.CMD_ACK:
                    affirmAck(sn, tag);
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
        buf.writeByte(tag);
        buf.writeByte(chunk.cmd);
        buf.writeLong(chunk.msgid);
        buf.writeInt(chunk.tot);
        buf.writeInt(chunk.all);
        buf.writeLong(chunk.ts);
        buf.writeInt(chunk.sn);
        buf.writeInt(chunk.una);
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

    private void affirmAck(int sn, byte tag) {
//        LOGGER.info("rudp2 affirmAck :" + " order size:" + rcvOrder.size() + " sb size:" + rcvShambles.size());
        if (sn == 0 && this.tag == RudpParam.TAG_NEW){
            this.tag = RudpParam.TAG_OLD;
        }
        synchronized (sndAckLock) {
            try {
                while (sndAckWait) {
                    sndAckLock.wait();
                }
                sndAckWait = true;
                if (sn == 0 && tag == RudpParam.TAG_OLD) {
                    this.tag = RudpParam.TAG_OLD;
                }
                sndAckList.removeIf(chunk -> chunk.sn == sn);
                if (sndAckList.isEmpty()) {
                    sndUna = sndNxt;
                } else {
                    sndUna = sndAckList.getFirst().sn;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                sndAckWait = false;
                sndAckLock.notifyAll();
            }
        }
    }

    private void dealTag(int sn, byte tag) {
    }

    private void affirmSnd(byte tag, long msgid, int tot, int all, long ts, int sn, int una, int length, byte[] data) {
        Chunk chunk = Chunk.newChunk(data);
        chunk.msgid = msgid;
        chunk.tot = tot;
        chunk.all = all;
        chunk.ts = ts;
        chunk.sn = sn;
        chunk.una = una;
        chunk.length = length;
//        LOGGER.info("rudp2 affirmSnd :" +  " order size:" + rcvOrder.size() + " sb size:" + rcvShambles.size());
        synchronized (rcvShamebleLock) {
            try {
                while (rcvShambleWait) {
                    rcvShamebleLock.wait();
                }
                rcvShambleWait = true;
                boolean exist = rcvShambles.stream().anyMatch(chunk1 -> chunk1.sn == sn);
                if (!exist) {
                    rcvShambles.add(chunk);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                rcvShambleWait = false;
                rcvShamebleLock.notifyAll();
            }
        }
        flushAck(sn);
    }

    private void flushAck(int sn) {
        Chunk chunk = Chunk.newChunk(new byte[0]);
        chunk.cmd = RudpParam.CMD_ACK;
        chunk.sn = sn;
        ByteBuf buf = createOutputByteBuf(chunk);
        sndChunk(buf, sn);
    }

    private void arrangeRcvShambles() {
        LinkedList<Chunk> addList = new LinkedList<>();
        synchronized (rcvShamebleLock) {
            try {
                while (rcvShambleWait) {
                    rcvShamebleLock.wait();
                }
                rcvShambleWait = true;
                for (Iterator<Chunk> itr = rcvShambles.iterator(); itr.hasNext(); ) {
                    Chunk chunk = itr.next();
                    if (chunk.sn == rcvNxt) {
                        addList.add(chunk);
                        itr.remove();
                        rcvNxt++;
                    } else if (chunk.sn < rcvNxt) {
                        itr.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                rcvShambleWait = false;
                rcvShamebleLock.notifyAll();
            }
        }
        synchronized (rcvOrderLock) {
            try {
                while (rcvOrderWait) {
                    rcvOrderLock.wait();
                }
                rcvOrderWait = true;
                rcvOrder.addAll(addList);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                rcvOrderWait = false;
                rcvOrderLock.notifyAll();
            }
        }
    }

    //返回可以提取的msgid
    public boolean canRcv() {
//        LOGGER.info("rudp2 canRcv:" + " order size:" + rcvOrder.size() + " sb size:" + rcvShambles.size());
        boolean canRcv = true;
        synchronized (rcvOrderLock) {
            try {
                while (rcvOrderWait) {
                    rcvOrderLock.wait();
                }
                rcvOrderWait = true;
                if (rcvOrder.isEmpty()) {
                    return false;
                }
                Chunk chunk = rcvOrder.getFirst();
                if (chunk.all == chunk.tot + 1) {
                    rcvOrderWait = false;
                    return true;
                }
                canRcv = rcvOrder.stream().anyMatch(chunk1 -> chunk1.tot == (chunk1.all - 1));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                rcvOrderWait = false;
                rcvOrderLock.notifyAll();
            }
        }
        return canRcv;
    }

    public Message mergeMsg() {
//        LOGGER.info("rudp2 mergeMsg :" + " order size:" + rcvOrder.size() + " sb size:" + rcvShambles.size());
        LinkedList<Chunk> chunks = new LinkedList<>();
        synchronized (rcvOrderLock) {
            try {
                while (rcvOrderWait) {
                    rcvOrderLock.wait();
                }
                rcvOrderWait = true;
                while (!rcvOrder.isEmpty()) {
                    Chunk chunk = rcvOrder.removeFirst();
                    chunks.add(chunk);
                    if (chunk.tot == (chunk.all - 1))
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                rcvOrderWait = false;
                rcvOrderLock.notifyAll();
            }

        }
        Message msg = null;
        if (!chunks.isEmpty()) {
            int length = chunks.stream().mapToInt(chunk -> chunk.length).sum();
            byte[] result = new byte[length];
            for (int i = 0; i < chunks.size(); i++) {
                System.arraycopy(chunks.get(i).data, 0, result, mtuBody * i, chunks.get(i).data.length);
            }
            msg = MessageUtil.bytes2Msg(result);
        }
        arrangeRcvShambles();
        return msg;
    }


    public boolean isStop() {
        return stop;
    }

    public int getInterval() {
        return interval;
    }

    public LinkedList<Chunk> getRcvOrder() {
        return rcvOrder;
    }

    public LinkedList<Chunk> getRcvShambles() {
        return rcvShambles;
    }
}
