package ppex.test;

import org.junit.Test;
import ppex.utils.ByteUtil;

public class ByteUtilTest {

    @Test
    public void ByteArrSplitTest(){
        byte[] src = new byte[4000];
        byte[][] result = ByteUtil.splitArr(src,1000);
        System.out.println("resule size:" + result.length);
    }
}
