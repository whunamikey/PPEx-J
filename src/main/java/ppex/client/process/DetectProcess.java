package ppex.client.process;

import io.netty.channel.Channel;
import ppex.client.entity.Client;
import ppex.utils.Constants;
import ppex.utils.MessageUtil;

public class DetectProcess {

    public boolean stop = false;
    public boolean step_one_running = true;
    public boolean step_two_running = true;


    private byte step;
    private boolean one_from_server1 = false;           //是否已经从server1返回信息
    private boolean one_from_server2p1 = false;         //是否已经从server2p1返回信息
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
            while (!stop && step_one_running) {
                if (stop){

                }
                Thread.sleep(2000);
            }
            two_send2s2p1();
            while (!stop && step_two_running) {
                Thread.sleep(2000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void one_send2s1() throws Exception {
        this.channel.writeAndFlush(MessageUtil.probemsg2Packet(MessageUtil.makeClientStepOneProbeTypeMsg(Client.getInstance().local_address, Constants.PORT1), Client.getInstance().SERVER1));
    }

    public void two_send2s2p1() throws Exception {
        this.channel.writeAndFlush(MessageUtil.probemsg2Packet(MessageUtil.makeClientStepTwoProbeTypeMsg(Client.getInstance().local_address, Constants.PORT1), Client.getInstance().SERVER2P1));
    }

    public boolean isOne_from_server1() {
        return one_from_server1;
    }

    public void setOne_from_server1(boolean one_from_server1) {
        this.one_from_server1 = one_from_server1;
    }

    public boolean isOne_from_server2p1() {
        return one_from_server2p1;
    }

    public void setOne_from_server2p1(boolean one_from_server2p1) {
        this.one_from_server2p1 = one_from_server2p1;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isStep_one_running() {
        return step_one_running;
    }

    public void setStep_one_running(boolean step_one_running) {
        this.step_one_running = step_one_running;
    }

    public boolean isStep_two_running() {
        return step_two_running;
    }

    public void setStep_two_running(boolean step_two_running) {
        this.step_two_running = step_two_running;
    }
}
