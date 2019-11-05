package ppex.proto.rudp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

//分片
public class Frg {
    private Frg(){}
    public static Frg createFrg(ByteBuf data){
        Frg frg = new Frg();
        frg.data = data;
        return frg;
    }

    public static Frg createFrg(ByteBufAllocator allocator,int size){
        Frg frg = new Frg();
        if (size == 0){
            frg.data = allocator.ioBuffer(0,0);
        }else{
            frg.data = allocator.ioBuffer(size);
        }
        return frg;
    }

    public void recycler(boolean release){
        if (data != null && data.refCnt() > 0)
            data.release();
    }

    public ByteBuf data;
    public byte cmd;
    public long msgid;
    public int tot;
    public int wnd;
    public long ts;
    public long sn;
    public long una;
    public long ts_resnd;
    public int rto;
    public int fastack;
    public int xmit;
    public long ackMask;
    public int ackMaskSize;

}
