package ppex.proto.pcp;

import io.netty.buffer.ByteBuf;

import java.util.Queue;

public class Pcp {


    //conv 会话,mtu最大传输单元大小,mss最大分节大小.mtu减去头部分
    private int conv,mtu,mss;
    //snd_una 已发送但未确认,snd_nxt下次发送下标,rcv_nxt,下次接收下标
    private int snd_una,snd_nxt,rcv_nxt;
    //ts_recent,ts_lastack 上次ack时间,ts_ssthresh 慢启动门限
    private int ts_recent,ts_lastack,ts_ssthresh;
    //rx_rttval RoundTripTime,rx_srtt 平滑rtt,rx_rto 重传超时,rxMinrto 最小重传超时
    private int rx_rttval,rx_srtt,rx_rto,rx_minrto;
    //snd_wnd 发送窗口,rcv_wnd 接收窗口,rmt_wnd 远端可接受端口,cwnd 拥塞控制窗口,probe 探测标志位
    private int snd_wnd,rcv_wnd,rmt_wnd,cwnd,probe;
    //current 当前时间,interval 间隔,ts_flush 发送时间戳,
    private int current,interval,ts_flush;
    //nodelay 收到包立即回ack, updated 状态是否更新
    private boolean nodelay,updated;
    //ts_probe 探测时间,probe_探测等待 probe_wait
    private long ts_probe,probe_wait;

    //
    private Queue<ByteBuf> snd_buf,rcv_buf;
    //待发送窗口和接收窗口
    private Queue<Fragment> snd_queue,rcv_queue;




}
