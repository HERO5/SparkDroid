package com.mrl.protocol.utils;

/**
 * @program: com.mrl.netty.common.utils
 * @description: 字节转换工具类
 * @author:
 * @create: 2020-02-01 15:14
 **/
public class ByteConvertUtil {
    private static int LONG_LENGTH = 8;
    private static int INT_LENGTH = 4;
    private static int SHORT_LENGTH = 2;
    private static int BYTE_LENGTH = 1;

    /**
     * Byte转换为有符号的Long类型，长度8字节 大端
     *
     * @param bytes 字节数组
     * @return 结果
     */
    public static long byteToSignedLongBE(byte[] bytes, int offset) {
        long result = 0;

        for (int i = offset; i < offset + LONG_LENGTH; i++) {
            result <<= (i == offset ? 0 : 8);
            result |= bytes[i] & 0xff;
        }
        return result;
    }

    /**
     * Byte转有符号转无符号的long类型 长度4字节 大端
     *
     * @param bytes  字节数组
     * @param offset 偏移量
     * @return 结果
     */
    public static long bytesToUnSignedIntBE(byte[] bytes, int offset) {
        long result = 0;

        for (int i = offset; i < offset + INT_LENGTH; i++) {
            result <<= (i == offset ? 0 : 8);
            result |= bytes[i] & 0xff;
        }
        return result;
    }

    /**
     * 无符号的int转为字节数组
     *
     * @param n      待转数字
     * @param b      需要装入的字节数组
     * @param offset 数字装入至数组的偏移量
     */
    public static void UnsignedIntToBytes(long n, byte[] b, int offset) {
        typeToBytesBE(n, b, INT_LENGTH, offset);
    }

    /**
     * 无符号的byte转为字节数组
     *
     * @param n      待转数字
     * @param b      需要装入的字节数组
     * @param offset 数字装入至数组的偏移量
     */
    public static void UnsignedShortToBytes(int n, byte[] b, int offset) {
        typeToBytesBE(n, b, SHORT_LENGTH, offset);
    }

    /**
     * 无符号的short转为字节数组
     *
     * @param n      待转数字
     * @param b      需要装入的字节数组
     * @param offset 数字装入至数组的偏移量
     */
    public static void UnsignedByteToBytes(short n, byte[] b, int offset) {
        typeToBytesBE(n, b, BYTE_LENGTH, offset);
    }

    /**
     * 将short、int、long型整型数字转换为指定长度（最大不超过8字节）的字节数组，大端形式存储
     *
     * @param n             待转数字
     * @param b             需要装入的字节数组
     * @param convertLength 转换的长度
     * @param offset        数字装入至数组的偏移量
     */
    public static void typeToBytesBE(long n, byte[] b, int convertLength, int offset) {
        if (b.length < offset + convertLength) {
            throw new IndexOutOfBoundsException("偏移量过大或转为字节数组的长度在字节数组中无法存放");
        }
        for (int i = convertLength - 1; i >= 0; i--) {
            b[i + offset] = (byte) (n & 0xff);
            n >>= 8;
        }
    }

    /**
     * 字节数组转化为有符号的int类型
     *
     * @param bytes  字节数组
     * @param offset 偏移地址
     * @return 返回值
     */
    public static int bytesToSignedIntBE(byte[] bytes, int offset) {
        int result = 0;
        for (int i = offset; i < offset + INT_LENGTH; i++) {
            result <<= (i == offset ? 0 : 8);
            result |= bytes[i];
        }
        return result;
    }

    /**
     * ByteBuf转换为有符号的Short类型转无符号的Int类型 长度2字节 大端
     *
     * @param bytes  字节数组
     * @param offset 偏移量
     * @return 结果
     */
    public static int bytesToUnSignedShortBE(byte[] bytes, int offset) {
        int result = 0;

        for (int i = offset; i < offset + SHORT_LENGTH; i++) {
            result <<= (i == offset ? 0 : 8);
            result |= bytes[i] & 0xff;
        }
        return result;
    }

    /**
     * ByteBuf转换为有符号的Byte类型转无符号的Short类型，长度1字节 大端
     *
     * @param bytes  字节数组
     * @param offset 偏移量
     * @return
     */
    public static short bytesToUnSignedByteBE(byte[] bytes, int offset) {
        short result = 0;

        for (int i = offset; i < offset + BYTE_LENGTH; i++) {
            result <<= (i == offset ? 0 : 8);
            result |= bytes[i] & 0xff;
        }
        return result;
    }

    public static String bytesToHEXString(byte[] bArray) {
        return bytesToHEXString(bArray, 0, bArray.length);
    }

    public static String bytesToHEXString(byte[] bArray, int offset, int length) {
        StringBuilder sb = new StringBuilder(100);
        for (int i = offset; i < (offset + length); i++) {
            String hex = Integer.toHexString(bArray[i] & 0xff);
            if (hex.length() == 1) {
                sb.append("0").append(hex);
            } else {
                sb.append(hex);
            }
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] HEXStringToBytes(String strString) {
        byte[] ret = new byte[strString.length() / 2];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Integer.decode("#" + strString.substring(2 * i, 2 * i + 2)).byteValue();
        }
        return ret;
    }

    /**
     * 元素 element 首次在数组中出现的下标
     *
     * @param bytes   数组
     * @param element 带查找元素
     * @return 首次出现的下标
     */
    public static int bytesIndexOfValue(byte[] bytes, byte element) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == element) {
                return i;
            }
        }
        return -1;
    }

    public static byte[] subBytes(byte[] bytes, int offset, int length) {
        byte[] subByteArray = new byte[length];
        System.arraycopy(bytes, offset, subByteArray, 0, length);
        /*
        * [nioEventLoopGroup-5-12] WARN io.netty.channel.DefaultChannelPipeline - An exceptionCaught() event was fired, and it reached at the tail of the pipeline. It usually means the last handler in the pipeline did not handle the exception.
java.lang.ArrayIndexOutOfBoundsException
	at java.lang.System.arraycopy(Native Method)
	at com.microthings.middleware.utils.ByteConvertUtil.subBytes(ByteConvertUtil.java:191)
        * */
        return subByteArray;
    }

    public static void main(String[] args) {
        byte[] bytes = HEXStringToBytes("ac100ef95e754b175770000000008102");
        System.out.println(bytes);
    }
}
