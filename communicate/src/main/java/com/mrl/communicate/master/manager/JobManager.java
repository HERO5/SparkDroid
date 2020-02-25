package com.mrl.communicate.master.manager;

import com.mrl.communicate.master.data.ResourceRepository;
import com.mrl.protocol.pojo.Task;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import io.netty.channel.ChannelHandlerContext;

/**
 * 1.后面会存在不同类型的任务，所以需要用不同的manager进行任务管理，这里把公共方法提取出来；
 *  在workerhandle中也有这样的考虑，只不过在那里是用继承workerhandler的方式实现的
 * 2.为了线程安全，最好不要在其他地方直接调用ResourceRepository
 */
public abstract class JobManager {

    private String source;

    public JobManager(String source){
        this.source = source;
    }

    protected void clean(){
        ResourceRepository.timeStart = -1;
        ResourceRepository.timeStop = -1;
        ResourceRepository.task.clear();
        ResourceRepository.workers.clear();
        ResourceRepository.taskWorker.clear();
        ResourceRepository.taskResultList.clear();
        ResourceRepository.taskResultMap.clear();
    }

    //生成 python task 的临时工具
    public void initTask(Object[] params){
        Task task = new Task();
        if(params!=null){
            String name = "_"+getRandomString(12);
            task.setId(name);
            task.setName(name);
            task.setParams(params);
            task.setFunc(source);
            task.setFuncName("main");
            ResourceRepository.task.add(task);
        }
    }

    public Task getTask(String workerId){
        if(ResourceRepository.timeStart<0) ResourceRepository.timeStart = new Date().getTime();
        Task task = null;
        //为了避免进入if以后，又变得不符合条件条件，所以要加锁
        //也为了避免同时为多个worker分配同一个task
        synchronized (ResourceRepository.task){
            if(ResourceRepository.task.size()>0){
                task = ResourceRepository.task.get(0);
                ResourceRepository.task.remove(task);
            }else {
                ResourceRepository.timeStop = new Date().getTime();
            }
        }
        if(task!=null){
            ResourceRepository.taskWorker.put(workerId, task);
        }
        return task;
    }

    public void addWorker(String workerId, ChannelHandlerContext ctx){
        ResourceRepository.workers.put(workerId, ctx);
    }

    public boolean removeWorker(String workerId){
        Task task = ResourceRepository.taskWorker.remove(workerId);
        if(task != null){
            ResourceRepository.task.add(task);
        }
        Object oj = ResourceRepository.workers.remove(workerId);
        if(oj != null){
            return true;
        }else {
            return false;
        }
    }

    public abstract boolean submitTask(String workerIp, Object res);

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

    public static String reportTime(){
        StringBuilder sb = new StringBuilder();
        sb.append("任务完成，耗时："+(ResourceRepository.timeStop-ResourceRepository.timeStart)+"ms\n");
        sb.append("Start:"+ResourceRepository.timeStart+"ms\n");
        sb.append("Stop:"+ResourceRepository.timeStop+"ms\n");
        return sb.toString();
    }

    public static String report(){
        StringBuilder sb = new StringBuilder();
        sb.append("ResourceRepository.task.size(): "+ResourceRepository.task.size());
        sb.append(" | ResourceRepository.taskWorker.size(): "+ResourceRepository.taskWorker.size());
        sb.append(" | ResourceRepository.taskResultMap.size(): "+ResourceRepository.taskResultMap.size());
        return sb.toString();
    }
}
