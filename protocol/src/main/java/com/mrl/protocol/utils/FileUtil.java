package com.mrl.protocol.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @program: com.mrl.netty.common.utils
 * @description:
 * @author:
 * @create: 2020-02-15 19:49
 **/
public class FileUtil {

    /**
     * 获取文件路径
     * @param file
     * @return
     */
    private static String getFilePath(String file){
        int last1 = file.lastIndexOf('/');
        int last2 = file.lastIndexOf('\\');
        return file.substring(0, last1>last2?last1:last2);
    }

    //读取一个文件的内容
    public static byte[] getBytes(String filename) throws IOException {
        File file=new File(filename);
        long len=file.length();
        byte[] raw=new byte[(int)len];
        FileInputStream fin =new FileInputStream(file);
        //一次读取class文件的全部二进制数据
        int r =fin.read(raw);
        if(r!=len)
            throw new IOException("无法读取全部文件："+r+" != "
                    +len);
        fin.close();
        return raw;
    }

    /**
     * 将String写入文件
     * @param file 文件名
     * @param source java代码
     * @throws Exception
     */
    public static void writeFile(String file,String source)throws Exception{
        BufferedWriter bw = null;
        try{
            File dir = new File(getFilePath(file));
            if(!dir.exists())
                dir.mkdirs();
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(source);
            bw.flush();
        }
        catch(Exception e){
            throw e;
        }
        finally{
            if(bw!=null){
                bw.close();
            }
        }
    }

    /**
     * 复制文件
     * @param fileIn 源文件绝对路径
     * @param fileOut 目的文件绝对路径
     */
    public static void copyFile(String fileIn, String fileOut) {
        File outfile = new File(fileOut);
        BufferedOutputStream outStream = null;
        BufferedInputStream inStream = null;

        try {
            outStream = new BufferedOutputStream(new FileOutputStream(outfile));
            inStream = new BufferedInputStream(new FileInputStream(fileIn));

            byte[] buffer = new byte[1024 * 10];
            int readLen = 0;
            while ((readLen = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, readLen);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) inStream.close();
                if (outStream != null) outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     * @param fileName
     * @return
     */
    public static boolean delFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        }

        if (file.isFile()) {
            return file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f.getAbsolutePath());
            }
            return file.delete();
        }
    }

    /**
     * 获取路径下的所有文件全名
     * @param path
     * @param listFileName
     */
    public static void getAllFileName(String path, ArrayList<String> listFileName) {
        File file = new File(path);
        File[] files = file.listFiles();
        String[] names = file.list();
        if (names != null) {
            String[] completNames = new String[names.length];
            for (int i = 0; i < names.length; i++) {
                completNames[i] = path + "/" + names[i];
            }
            listFileName.addAll(Arrays.asList(completNames));
        }
        for (File a : files) {
            if (a.isDirectory()) {//如果文件夹下有子文件夹，获取子文件夹下的所有文件全路径。
                getAllFileName(a.getAbsolutePath() + "/", listFileName);
            }
        }
    }

}
