package com.mrl.communicate.master.manager;

import com.mrl.communicate.master.data.ResourceRepository;
import com.mrl.protocol.pojo.Task;

import java.util.Random;

/**
 * @deprecated
 * @program: com.mrl.netty.server.service
 * @description: 划分Job为小的Task
 * @author:
 * @create: 2020-02-01 15:30
 **/
public class JobManager1 {

    /**
     * 为了线程安全，最好不要在其他地方直接调用ResourceRepository
     */

    //生成 python task 的临时工具
    public static void initTask(int count, String source) {
        Task task = new Task();
        Object[] data = null;
        double max = 22.0;
        double min = 11.0;
        if(source==null){
            StringBuilder sb = new StringBuilder();
            sb.append("def main(params) :\n");
            sb.append("    int = params[0]+params[1]\n");
            sb.append("    str = params[2]\n");
            sb.append("    print(\"res:\", int, str)\n");
            sb.append("    return int");
            source = sb.toString();
            data = new Double[2];
            //准备测试数据
            data[0] = nextDouble(min - 1, max + 1);
            data[1] = nextDouble(min - 1, max + 1);
            data[2] = "hello, it is SparkDroid";
        }
        for(int i=0; i<count; i++){
            String name = "_"+getRandomString(12);
            task.setId(name);
            task.setName(name);
            task.setParams(data);
            task.setFunc(source);
            task.setFuncName("main");
            ResourceRepository.task.add(task);
        }
        return;
    }

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
    public static double nextDouble(final double min, final double max) {
        if (min == max) {
            return min;
        }
        return min + ((max - min) * new Random().nextDouble());
    }

    public static Task getTask(String workerId){
        Task task = null;
        //为了避免进入if以后，又变得不符合条件条件，所以要加锁
        //也为了避免同时为多个worker分配同一个task
        synchronized (ResourceRepository.task){
            if(ResourceRepository.task.size()>0){
                task = ResourceRepository.task.get(0);
                ResourceRepository.task.remove(task);
            }
        }
        if(task!=null){
            ResourceRepository.taskWorker.put(workerId, task);
        }
        return task;
    }

    public static boolean submitTask(String workerIp, Object res) {
        Task task = ResourceRepository.taskWorker.get(workerIp);
        ResourceRepository.taskWorker.remove(workerIp);
        //判断任务完成的是否合格
        boolean complete = nextDouble(0.0, 10.0)>5;
        System.out.println(res+"; "+complete);
        if(res!=null && complete){
            ResourceRepository.taskResultMap.put(task.getId(), res);
        }else{
            ResourceRepository.task.add(task);
        }
        return complete;
    }

    public static String report(){
        StringBuilder sb = new StringBuilder();
        sb.append("ResourceRepository.task.size(): "+ResourceRepository.task.size());
        sb.append(" | ResourceRepository.taskWorker.size(): "+ResourceRepository.taskWorker.size());
        sb.append(" | ResourceRepository.taskResultMap.size(): "+ResourceRepository.taskResultMap.size());
        return sb.toString();
    }
}
