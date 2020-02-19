package com.mrl.communicate.worker.handler;

import android.os.Handler;

import com.mrl.communicate.business.Executor;
import com.mrl.communicate.middle.ResultOfCall;
import com.mrl.protocol.pojo.Task;
import com.mrl.protocol.pojo.message.Message;
import com.mrl.protocol.pojo.message.MessageContent;

import io.netty.channel.ChannelHandlerContext;

/**
 * COMPLETE: 本次获取到的Task已经完全处理好了
 * INTERMISSION: 本次处理没有得到最终结果，而是产生了大批中间数据，此时把这些中间数据传回master，让更多worker处理
 * 这种workerhandler适用于处理像广度优先搜索这样的工作。其实这里的处理方式和JobManager2中submitTask的处理方式有很强的逻辑关联
 */
public class IterationWorkerHandler extends WorkerHandler {

    public IterationWorkerHandler(Handler handler, Executor executor) {
        super(handler, executor);
    }

    @Override
    public void onTaskPut(ChannelHandlerContext ctx, Message message){

        /**
         * 如何判定一个处理是成功的？首先result不能为null，而且必须是ResultOfCall类型的，这样就可以了，暂时这么设定
         * 另外complete和intermission可以同时为空。因为可能存在这种情况：任务本身无解，worker已经尽力了
         */
        Task taskPut = (Task) message.getMessageContent().getContent();
        if(taskPut!=null){
            Object result = executor.exec(taskPut.getFunc(), taskPut.getFuncName(), taskPut.getParams());
            Message res = Message.SUBMIT;
            Task task = new Task();
            if(result!=null && result instanceof ResultOfCall){
                task.setState(Task.SUCCESS);
                Object[] complete = ((ResultOfCall) result).getComplete();
                Object[] intermission = ((ResultOfCall) result).getIntermission();
                if(complete!=null && complete.length>0){
                    task.setComplete(complete);
                }
                if(intermission!=null){
                    task.setIntermission(intermission);
                }
            }else {
                task.setState(Task.FAILED);
            }
            res.setMessageContent(new MessageContent(task));
            sendMsg("任务处理: "+taskPut.getName()+"."+taskPut.getFuncName()+"("+taskPut.getParams()+")"+" -> "+result, true);
            ctx.writeAndFlush(res);
        }
    }
}
