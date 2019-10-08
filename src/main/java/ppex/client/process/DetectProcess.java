package ppex.client.process;

import io.netty.channel.Channel;
import ppex.client.entity.Client;
import ppex.proto.type.ProbeTypeMsg;
import ppex.utils.Constants;
import ppex.utils.MessageUtil;


/**
 * Device探测自己的NAT类型
 *根据工作原理.md里面的记录.19-10-8对第一阶段和第二阶段的优化记录.
 * 第一阶段和第二阶段的探测就不用隔开一段时间,需要Server1:Port1向Server2:Port2发送包
 */
public class DetectProcess {

    public boolean stop = false;

    private byte step;
    private boolean one_from_server1 = false;           //是否已经从server1返回信息
    private boolean one_from_server2p2 = false;         //是否已经从server2p1返回信息
    private boolean two_from_server2p1 = false;         //是否已经从Server2p1返回信息
    private boolean two_from_server2p2 = false;         //是否已经从Server2p2返回信息

    private Channel channel;

    public static DetectProcess instance = null;

    private DetectProcess() {
    }

    public static DetectProcess getInstance(){
        if (instance == null)
            instance = new DetectProcess();
        return instance;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void startDetect() {
        try {
            one_send2s1();
            two_send2s2p1();
//            while (!stop) {
//                System.out.println("Client sleep 2000");
//                Thread.sleep(2000);
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void one_send2s1() throws Exception {
        this.channel.writeAndFlush(MessageUtil.probemsg2Packet(MessageUtil.makeClientStepOneProbeTypeMsg(Client.getInstance().local_address, Constants.PORT1), Client.getInstance().SERVER1));
    }

    public void two_send2s2p1() throws Exception {
        this.channel.writeAndFlush(MessageUtil.probemsg2Packet(MessageUtil.makeClientStepTwoProbeTypeMsg(Client.getInstance().local_address, Constants.PORT1), Client.getInstance().SERVER2P1));
        System.out.println("send2s2p1...");
    }

    public boolean isOne_from_server1() {
        return one_from_server1;
    }

    public void setOne_from_server1(boolean one_from_server1) {
        this.one_from_server1 = one_from_server1;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

}
