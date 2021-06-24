package cn.com.lee.nio.simpleDemo.channel;

import org.junit.jupiter.api.Test;
import sun.nio.ch.IOUtil;

import java.io.*;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * channel : 对底层的文件描述符的抽象，如硬件设备，文件，网络连接等；
 * 不同的网络传输协议类型也有不同的channel实现
 * <p>
 * FileChannel: 用于文件读取，它是阻塞的
 */
public class UseFileChannel {
    /**
     * 根据文件流获取FileChannel
     *
     * @return
     */
    FileChannel getFileChannelWithStream() {
        FileChannel fileChannel = null;
        URL resource = UseFileChannel.class.getClassLoader().getResource("fileChannel.txt");
        try {
            FileInputStream fis = new FileInputStream(resource.getFile());

            fileChannel = fis.getChannel();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileChannel;
    }

    /**
     * 读取FileChannel里面的文件数据
     */
    @Test
    public void testRead() {
        FileChannel fileChannel = getFileChannelWithStream();
        //创建一个Buffer实例
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024); //这个和
        //读取通道的数据，对于缓冲区而言是写，创建一个缓冲区默认是写模式
        int length = -1;

        try {
            while ((length = fileChannel.read(byteBuffer)) != -1) {
                byteBuffer.flip(); //转成读模式
                System.out.println(new String(byteBuffer.array()));
                byteBuffer.clear();//转成写模式
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

    }

    /**
     * 将字符串写入文件中
     */
    @Test
    public void testWrite() {
        FileOutputStream fos = null;
        try {
            URL resource = this.getClass().getClassLoader().getResource("");
            System.out.println(resource.getPath());
            fos = new FileOutputStream(new File(resource.getPath(), "fileChannel_1.txt"));
            FileChannel channel = fos.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byte[] bytes = "你好，这是我用FileChannel生成的文件！".getBytes(StandardCharsets.UTF_8);
            byteBuffer.put(bytes);
            byteBuffer.flip(); //转成读取模式了
            int length = -1;
            channel.write(byteBuffer);
            channel.force(true);
            channel.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将源文件拷贝到另一个文件中
     */
    @Test
    public void nioCopyResourceFile() {
//        System.out.println(System.getProperty("user.dir"));
        File parentPath = new File(System.getProperty("user.dir"), "src\\main\\resources\\fileChannel");
//        System.out.println(parentPath.getPath());
        File srcFile = new File(parentPath, "srcFile.txt");
        System.out.println("源文件" + srcFile.getPath());
        File distFile = new File(parentPath, "distFile.txt");
        System.out.println("目标文件：" + distFile.getPath());
        RandomAccessFile accessSrcFile = null;
        RandomAccessFile accessDistFile = null;
        FileChannel srcFileChannel = null;
        FileChannel distFileChannel = null;
        try {
            if (!distFile.exists()) {
                distFile.createNewFile();
            }
            accessSrcFile = new RandomAccessFile(srcFile, "rw");
            accessDistFile = new RandomAccessFile(distFile, "rw");

            srcFileChannel = accessSrcFile.getChannel();
            distFileChannel = accessDistFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024); //写模式
            //scrFileChannel -> byteBuffer -> distFileChannel ; byteBuffer.clear()
            int length = -1;
            while ((length = srcFileChannel.read(byteBuffer)) != -1) { //scrFileChannel -> byteBuffer
                byteBuffer.flip();//读模式了
                int outLength = 0;
                while ((outLength = distFileChannel.write(byteBuffer)) != 0) {
                    System.out.println("写入的字节数：" + outLength);
                }

                byteBuffer.clear();
            }
            distFileChannel.force(true);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {//如何优雅的关闭？
            //先关闭通道，再关闭流;先关闭输出流再关闭输入流？
            try {
                distFileChannel.close();
                accessDistFile.close();
                srcFileChannel.close();
                accessSrcFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
