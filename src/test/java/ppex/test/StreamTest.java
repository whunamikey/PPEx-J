package ppex.test;

import org.junit.Test;
import ppex.proto.rudp2.Chunk;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.IntStream;

public class StreamTest {

    @Test
    public void LinkedListTest(){
        LinkedList<String> strs = new LinkedList<>();
        IntStream.range(0,100).forEach(val -> strs.add("val:" + val));
        while(!strs.isEmpty()){
            String val = strs.removeFirst();
            System.out.println(val);
        }
    }

    @Test
    public void ItemModifyTest(){
//        LinkedList<String> strs = new LinkedList<>();
//        IntStream.range(0,100).forEach(val -> strs.add("val" + val));
//        strs.forEach(str -> str = str+"...........");
//        for (Iterator<String> itr = strs.iterator();itr.hasNext();){
//            String str = itr.next();
//            str += ".......";
//        }
//        strs.forEach(str -> System.out.println("str:" + str));

//        LinkedList<Integer> integers = new LinkedList<>();
//        IntStream.range(0,100).forEach( val -> integers.add(val));
//        integers.forEach(val -> val = 2);
//        integers.forEach(val -> System.out.println("val:" + val));

        LinkedList<Chunk> chunks = new LinkedList<>();
        IntStream.range(0,100).forEach(val ->{
            Chunk chunk = Chunk.newChunk(new byte[0]);
            chunk.sn = val;
            chunks.add(chunk);
        });
        chunks.forEach(chunk -> chunk.sn = 1);
        chunks.forEach(chunk -> System.out.println("chunk sn:" + chunk.sn));

    }

}
