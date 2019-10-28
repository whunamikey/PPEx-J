package ppex.proto.pcp;

import io.netty.buffer.ByteBuf;
import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.SpscArrayQueue;

import java.util.Queue;

public class PcpPack {
    private final Pcp pcp;
    private PcpListener pcpListener;

    private final Queue<ByteBuf> rcvList;
    private final MpscArrayQueue<ByteBuf> sndList;

    public PcpPack(PcpListener pcpListener) {
        this.pcp = new Pcp();
        sndList = new MpscArrayQueue<>(2 << 11);
        rcvList = new SpscArrayQueue<>(2 << 11);
    }


    public void send(ByteBuf byteBuf){
        
    }


}
