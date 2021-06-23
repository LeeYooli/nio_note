package cn.com.lee.nio.simpleDemo;

import org.junit.jupiter.api.Test;

import java.nio.Buffer;
import java.nio.IntBuffer;

/**
 * Buffer 通过allcate()创建Buffer实例
 * Buffer 有8个子类，一次是Byte,MappedByte,Char,Short,Integer,Long,Float,Double
 * 它有capacity，limit，position3个重要的属性
 * allocate(),put(),flip(),clear(),reset(),rewind()等方法。
 */
public class BufferUsage {

    /**
     * rewind --一定要在读模式下面操作！
     * 已读完的数据再读一遍
     */
    @Test
    public void rewindTest(){
        IntBuffer intBuffer = getIntBuffer();
        intBuffer.flip();//如果不调用flip方法，那么下面的操作会让position继续累加
        for(int i = intBuffer.position(); i<intBuffer.limit();i++){
            System.out.println(intBuffer.get());
        }
        System.out.println("---遍历intBuffer后---");

        printBufferInfo(intBuffer);
        /**
         * position = 0;
         * mark = -1;
         */
        intBuffer.rewind();//会重置position ,清除mark
        System.out.println("---调用rewind()方法后---");
        printBufferInfo(intBuffer);


    }

    /**
     * 读的时候标记和reset
     */
    @Test
    public void markAndResetTest(){
        IntBuffer intBuffer = getIntBuffer();
        intBuffer.flip();//转成读模式
        for(int i = 0; i<2;i++){
            System.out.println(intBuffer.get());
        }
        System.out.println("---读取2个元素以后---");
        printBufferInfo(intBuffer);

        //mark = position;
        intBuffer.mark();
        System.out.println("---调用mark()方法后---");
        printBufferInfo(intBuffer); //position = 2

        for(int i = 0; i<2;i++){
            System.out.println(intBuffer.get());
        }
        System.out.println("---再读取2个元素以后---");
        printBufferInfo(intBuffer); //position = 4
        //position = mark;
        intBuffer.reset();
        System.out.println("---调用reset()以后---");
        printBufferInfo(intBuffer); //position = 2


    }

    private static IntBuffer getIntBuffer(){
        IntBuffer intBuffer = IntBuffer.allocate(20);
        System.out.println("------------创建buffer实例后------------");
        printBufferInfo(intBuffer);
        for(int i = 0; i<5;i++){//通过put方法写入5个元素
            intBuffer.put(i);
        }
        System.out.println("---写入5个元素后---");
        printBufferInfo(intBuffer);
        return intBuffer;
    }
    private static void printBufferInfo(Buffer buffer) {
        System.out.println("开始打印buffer的信息：");
        System.out.println("-->position = " + buffer.position());
        System.out.println("-->limit = " + buffer.limit());
        System.out.println("-->capacity = " + buffer.capacity());
        System.out.println("buffer的信息打印结束！");

    }

    public static void main(String[] args) {
        //创建一个Buffer实例
        IntBuffer intBuffer = IntBuffer.allocate(20); //capacity=20，limit=20,position=0,写模式
        System.out.println("------------创建buffer实例后------------");
        printBufferInfo(intBuffer);
        for(int i = 0; i<5;i++){//通过put方法写入5个元素
            intBuffer.put(i);
        }
        //是否可读
//        System.out.println(intBuffer.get()); //这个时候调用get，会让position+1
        System.out.println(intBuffer.get(1)); //这个时候调用get(index)， 不会影响position
        System.out.println("------------写入5个元素以后------------");
        printBufferInfo(intBuffer); //capacity=20，limit=20,position=5,写模式:position指向下一个要写入的元素的下标;put只改变position的值
        /**
         * limit = position;
         * position = 0;
         * mark = -1;
         */
        intBuffer.flip();
        System.out.println("------------调用flip方法后------------");
        printBufferInfo(intBuffer); //capacity=20,limit=5,position=0,读模式： capacity的值不变，limit变成了position的值，position=0；mark=-1
        for(int i = intBuffer.position(); i <3; i++){
            System.out.println(intBuffer.get()); //get方法只改变position方法
        }
        System.out.println("------------读取3个元素以后------------");
        printBufferInfo(intBuffer); //capacity=20,limit=5,position=3,读模式： capacity的值不变，limit不变，position=0；mark=-1
//        System.out.println(intBuffer.get(5)); //get(5)会报错，看来这个是数组下标index
        System.out.println(intBuffer.get(2)); //
        System.out.println("------------读取get(2)以后------------");
        printBufferInfo(intBuffer); //调用get(2)没有改变capacity，position,limit的值
        for(int i = 0; i <2; i++){
            System.out.println(intBuffer.get()); //get方法只改变position方法
        }
        System.out.println("------------再读取2个元素以后------------");
        printBufferInfo(intBuffer); //capacity=20,limit=5,position=5
        intBuffer.flip();
        System.out.println("------------调用flip方法后------------");
        /**
         *  limit = position;
         *  position = 0;
         *  mark = -1;
         */
        printBufferInfo(intBuffer); //capacity=20,limit=5,position=0 此时limit=5代表什么？--代表只能存5个元素了
//        for(int i = 0; i<6;i++){ //报错：Exception in thread "main" java.nio.BufferOverflowException
//            intBuffer.put(i);
//        }

        /**
         * position = 0;
         * limit = capacity;
         * mark = -1;
         */
        intBuffer.clear();
        System.out.println("------------调用clear方法后------------");
        printBufferInfo(intBuffer); //clear以后posotion置为0，limit置为capacity，mark清空 此时换成了写模式


        for(int i = 0; i<9;i++){ //再写入9个元素
            intBuffer.put(i);
        }
        System.out.println("------------调用clear方法，写入9个元素后------------");
        printBufferInfo(intBuffer);//capacity=20，limit=20,position=9;

//        System.out.println(intBuffer.get());//如果此时调用了get方法,position会+1；但是调用get(index)不会影响position
//        System.out.println("------------调用clear方法，写入9个元素,调用get()后------------");
//        printBufferInfo(intBuffer);//capacity=20，limit=20,position=10; //明显有问题，也就是说put和get不能同时用，不然会出错。


        intBuffer.compact();
        System.out.println("------------调用compact方法后------------");
        printBufferInfo(intBuffer); //clear以后posotion置为0，limit置为capacity，mark清空
        System.out.println(intBuffer.isReadOnly());




    }
}
