package ppex.proto.rudp2;

/**
 * udp传输设置,看Frg属性
 * +-----8bit----+-----64bit----+----32bit-----+---32bit---+--64bit---+----64bit----+----64bit----+----32bit----+
 * +     cmd     +     msg id   +     tot      +    wnd    +   ts     +     sn      +     una     +    length   +
 * +-------------+--------------+--------------+-----------+----------+-------------+-------------+-------------+
 *  cmd表示是收还是发.看RudpParam中的CMD_SND,CMD_ACK,CMD_START,CMD_FINISH.
 *  msg id表示该分段属于哪一个msg
 *  tot 表示该msg中的第几个段,倒着表示.0表示是该msg的最后一个段
 *  wnd 表示两边的处理窗口长度,就相当于宽带
 *  ts 表示该段的时间戳
 *  sn 表示该段的编号.一个Rudp开始从0开始一直增加.
 *  una 表示未确认的sn编号.例如发出去了sn为0的一个,但是没有收到ack,那么una就为0
 *  length 表示后面的数据长度,可以使用short简化,因为不会超过MTU_DEFAULT
 *
 *  1+8+4+4+8+8+8+4=45个字节,后面可以根据实际优化msg id,tot,wnd,sn,una,length.都可以变成short以及int
 */

public class RudpParam {

    public static final int NO_DEFILE_RTO = 30;
    public static final int RTO_MIN = 100;
    public static final int RTO_DEFAULT = 200;
    public static final int RTO_MAX = 30000;

    public static final byte CMD_SND = 1;
    public static final byte CMD_ACK = 2;
    public static final byte CMD_START = 3;
    public static final byte CMD_FINISH = 4;

    //发送超过20次还没收到ack就相当于连接断开
    public static final int DEAD_LINK = 20;

    //头部数据长度
    public static final int HEAD_LEN = 45;
    //MTU默认长度
    public static final int MTU_DEFAULT = 1445;
    //除去头部数据长度之后剩下长度
    public static final int MTU_BODY =  MTU_DEFAULT - HEAD_LEN;

    //默认窗口
    public static final int WND_SND = 32;
    public static final int WND_RCV = 32;


}
