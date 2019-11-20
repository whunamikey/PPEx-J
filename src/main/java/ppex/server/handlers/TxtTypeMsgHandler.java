package ppex.server.handlers;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import ppex.proto.msg.Message;
import ppex.proto.msg.entity.testpack.Files;
import ppex.proto.msg.entity.txt.Request;
import ppex.proto.msg.entity.txt.Response;
import ppex.proto.msg.type.TxtTypeMsg;
import ppex.proto.msg.type.TypeMessage;
import ppex.proto.msg.type.TypeMessageHandler;
import ppex.proto.rudp.IAddrManager;
import ppex.proto.rudp.Rudp;
import ppex.proto.rudp.RudpPack;
import ppex.utils.MessageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TxtTypeMsgHandler implements TypeMessageHandler {

    private static Logger LOGGER = Logger.getLogger(TxtTypeMsgHandler.class);


    @Override
    public void handleTypeMessage(ChannelHandlerContext ctx, RudpPack rudpPack, IAddrManager addrManager, TypeMessage tmsg) {
        LOGGER.info("TxtTypemsg handle:" + tmsg.getBody());
        TxtTypeMsg txtTypeMsg = JSON.parseObject(tmsg.getBody(), TxtTypeMsg.class);
        if (txtTypeMsg.isReq()){
            RudpPack torudppack = addrManager.get(txtTypeMsg.getTo());
            torudppack.write(MessageUtil.txtmsg2Msg(txtTypeMsg));
        }else{
            RudpPack fromrudppack = addrManager.get(txtTypeMsg.getFrom());
            fromrudppack.write(MessageUtil.txtmsg2Msg(txtTypeMsg));
        }
//        if (txtTypeMsg.isReq()) {
//            txtTypeMsg.setReq(false);
//            File file = new File("D:/");
//            File[] files = file.listFiles();
//            List<Files> fileList = new ArrayList<>();
//            Arrays.stream(files).forEach(file1 -> {
//                Files files1 = new Files();
//                files1.setDirectory(file1.isDirectory());
//                files1.setName(files1.getName());
//                fileList.add(files1);
//            });
//            Response response = new Response();
//            response.setBody(JSON.toJSONString(fileList));
//            response.setHead("/file/getfiles\r\n");
//            txtTypeMsg.setContent(JSON.toJSONString(fileList));
//            rudpPack.write(MessageUtil.txtmsg2Msg(txtTypeMsg));
//        }
    }
}
