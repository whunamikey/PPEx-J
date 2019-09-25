package ppex.proto.type;

import ppex.utils.MessageUtil;

import java.net.InetSocketAddress;

public class ProbeMessage implements TypeMessage {

    @Override
    public void handleTypeMessage(TypeMsg msg) {
        if (msg.getType() != MessageUtil.MsgType.MSG_TYPE_PROBE)
            return;
    }

    /**
     * 区分这个ProbeMessage是从client还是server来
     */
    private boolean fromClient;
    private InetSocketAddress inetSocketAddress;
    private String body;
}
