package ppex.test;

import org.junit.Test;
import ppex.utils.ByteUtil;

import java.util.LinkedList;
import java.util.stream.IntStream;

public class ByteUtilTest {

    @Test
    public void ByteArrSplitTest(){
        byte[] src = new byte[4000];
        byte[][] result = ByteUtil.splitArr(src,1000);
        System.out.println("resule size:" + result.length);
    }

    @Test
    public void ByteArrMergeTest(){
        byte[] src = new byte[20000];
        byte[][] result = ByteUtil.splitArr(src,1223);
        byte[] merge = ByteUtil.mergeArr(result,1223);
        System.out.println("merge length:" + merge.length);
    }


}
