package ppex.proto.pcp;

import io.netty.buffer.ByteBuf;
import io.netty.util.Recycler;

public class Fragment {
    private final Recycler.Handle<Fragment> recyclerHandler;
    private static final Recycler<Fragment> RECYCLER = new Recycler<Fragment>() {
        @Override
        protected Fragment newObject(Handle<Fragment> handle) {
            return new Fragment(handle);
        }
    };
    private Fragment(Recycler.Handle<Fragment> recyclerHandler){
        this.recyclerHandler = recyclerHandler;
    }

    public static Fragment createFragment(ByteBuf data){
        Fragment fragment = RECYCLER.get();
        fragment.data = data;
        return fragment;
    }

    private int conv;
    private byte cmd;                       //命令
    private short frgid;                    //message分片后的fragmentid
    private int wnd;                        //剩余接收窗口大小
    private long ts;                        //message发送的时间戳
    private long sn;                        //message分片fragment的序号
    private long una;                       //待接收消息序号,接收滑动窗口左端
    private long resendts;                  //下次超时重传时间戳
    private int rto;                        //该分片的超时重传等待时间
    private int fastack;                    //收到ack时计算该分片被跳过的累计次数,即该分片后面都接收到了,达到一定次数,重传它
    private int xmit;                       //发送分片的次数,每发送一次加1
    private long ackMask;
    private ByteBuf data;

}