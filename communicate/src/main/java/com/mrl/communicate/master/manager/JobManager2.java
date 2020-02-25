package com.mrl.communicate.master.manager;

import com.mrl.communicate.master.data.ResourceRepository;
import com.mrl.protocol.pojo.Task;

import java.util.Collections;

/**
 * @program: com.mrl.netty.server.service
 * @description: 任务管理器
 * @author:
 * @create: 2020-02-01 15:30
 **/
public class JobManager2 extends JobManager {

    //此参数用来指定打包新任务时，一个新任务包含的参数个数
    public static int paramsNum = 100;

    //初始化一个master时一定要先清理ResourceRepository
    public JobManager2(String source, Object[] params){
        super(source);
        clean();
        initTask(params);
    }

    /**
     * 1.COMPLETE: res是最终结果，可以直接存入resultList
     * 2.INTERMISSION: 这个Task没有处理完毕，而是产生了大批中间数据，把这些中间数据作为新的参数,封装成新的Task，让更多worker处理
     * 这种workerhandler适用于处理像广度优先搜索这样的工作，这里的处理方式和IterationWorkerHandler的处理方式有很强的逻辑关联
     * 3.如何判定一个提交是成功的？首先得到的res不为null，而且必须是Task类型，另外res的状态码还得是SUCCESS。暂时这么设定
     */
    @Override
    public boolean submitTask(String workerIp, Object res) {
//        Task task = ResourceRepository.taskWorker.get(workerIp);
        Task task = ResourceRepository.taskWorker.remove(workerIp);
        boolean success = false;
        if(res!=null && res instanceof Task) {
            switch (((Task) res).getState()){
                case Task.SUCCESS:
                    Object[] complete = ((Task) res).getComplete();
                    Object[] intermission = ((Task) res).getIntermission();
                    if(complete!=null && complete.length>0){
                        Collections.addAll(ResourceRepository.taskResultList, complete);
                    }
                    if(intermission!=null && intermission.length>0){
                        //下面属于任务分割的一部分，将worker返回的中间数据按批次放入Task中，批次大小对效率影响很大
                        int count = 0;
                        Object [] params = new Object[paramsNum];
                        for(int i=0; i<intermission.length; i++){
                            params[count++] = intermission[i];
                            if(count>= paramsNum || (intermission.length == (i+1))){
                                if(count < paramsNum){
                                    Object[] params2 = new Object[count];
                                    for(int j=0;j<count;j++){
                                        params2[j] = params[j];
                                    }
                                    params = params2;
                                }
                                initTask(params);
                                count = 0;
                                params = new Object[paramsNum];
                            }
                        }
                    }
                    success = true;
                    break;
                default:
                    break;
            }
        }
        if(!success){
            ResourceRepository.task.add(task);
        }
        return success;
    }
}
