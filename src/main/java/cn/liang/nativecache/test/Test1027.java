package cn.liang.nativecache.test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2016/10/27.
 */
public class Test1027 {

    public static void main(String[] args) {
        try {
//            read();
            readCy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void read() throws IOException {
        File file = new File("d://test/武汉.txt");
        FileChannel channel = FileChannel.open(file.toPath());
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        channel.read(byteBuffer);
        byteBuffer.flip();
        while (byteBuffer.remaining() > 0) {
            System.out.println(byteBuffer.getChar());
        }
    }

    private static void readCy() throws IOException {
        File file = new File("d://test/武汉.txt");
        FileChannel channel = FileChannel.open(file.toPath());
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        int length = 0;
        List<Byte> list = new ArrayList<>();
        while ((length = channel.read(byteBuffer)) > 0) {
            byteBuffer.flip();
            while (byteBuffer.remaining() > 0) {
                list.add(byteBuffer.get());
//                System.out.println(byteBuffer.get());
            }
            byteBuffer.compact();
//            System.out.println("length="+length);
        }
        Byte[] bytes = list.toArray(new Byte[0]);
        byte[] bytes1 = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes1[i] = bytes[i];
        }
        System.out.println(new String(bytes1));
    }
}
