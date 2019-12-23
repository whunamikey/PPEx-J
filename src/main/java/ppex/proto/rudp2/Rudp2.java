package ppex.proto.rudp2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import ppex.proto.msg.Message;
import ppex.proto.rudp.IOutput;
import ppex.utils.MessageUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * 2019-12-23.暂不考虑其他重传算法以及RTT,RTO等时间计算.直接简单粗暴发送与接收
 */
public class Rudp2 {

    private ByteBufAllocator byteBufAllocator = PooledByteBufAllocator.DEFAULT;

    //发送数据与接收数据的集合
    private List<Chunk> sndList = new LinkedList<>();
    private List<Chunk> sndAckList = new LinkedList<>();
    private List<Chunk> rcvOrder = new LinkedList<>();
    private List<Chunk> rcvShambles = new LinkedList<>();

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

    //发送数据公共接口
    private IOutput output;

    public Rudp2(IOutput output) {
        this.output = output;
        sndNxt = 0;
        sndUna = 0;
        rcvNxt = 0;
    }

    public boolean send(Message msg){
        ByteBuf buf = MessageUtil.msg2ByteBuf(msg);
        int len = buf.readableBytes();
        if (len == 0){
            buf.release();
            return true;
        }
        int count = 0;
        if (len < mtuBody){
            count = 1;
        }else{
            count = (len + mtuBody - 1) / mtuBody;
        }
        if (count == 0)
            count = 1;
        for (int i = 0;i < count;i++){
            int bufSize = len > mtuBody ? mtuBody : len;

        }
        return true;
    }

}
