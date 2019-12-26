package ppex.utils;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 读取文件排序
 */
public class SortedUtil {
    public static void main(String[] args) {
        readFile();
    }

    public static void readFile() {
        String filename = "C:\\Users\\jinglv\\Desktop\\addsn.txt";
        BufferedReader br = null;
        try {
            File file = new File(filename);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            List<Integer> sn = new LinkedList<>();
            String s;
            while ((s = br.readLine()) != null) {
                sn.add(Integer.valueOf(s));
            }
            Collections.sort(sn);
            sn.forEach(val -> System.out.println(sn));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
