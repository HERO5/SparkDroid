package com.mrl.communicate.worker.handler;

import android.os.Handler;
import android.util.Log;

import com.mrl.communicate.business.Executor;
import com.mrl.protocol.pojo.message.Message;
import com.mrl.protocol.pojo.message.MessageType;

import java.util.Date;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @program: com.mrl.netty.client.handler
 * @description:
 * @author:
 * @create: 2020-02-01 18:53
 **/
public abstract class WorkerHandler extends ChannelInboundHandlerAdapter {

    private Handler handler;
    protected Executor executor;

    public WorkerHandler(Handler handler, Executor executor) {
        this.handler = handler;
        this.executor = executor;
    }

    //通道就绪事件
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        sendMsg("client login", true);
        Message message = Message.TASK_GET;
        ctx.writeAndFlush(message);
    }

    //读取数据事件
    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg){
        Message message=(Message) msg;
        sendMsg("服务器端发来的消息："+message.getMessageHeader().toString(), true);
        int type = message!=null?message.getMessageHeader().getMessageType():0;
        switch (type){
            case MessageType.TASK_PUT:
                onTaskPut(ctx, message);
                break;
            case MessageType.TASK_CONFIRM:
                sendMsg("结果得到确认", true);
                ctx.writeAndFlush(Message.TASK_GET);
                break;
            default:
                ctx.writeAndFlush(Message.TASK_GET);
                break;
        }
    }

    public abstract void onTaskPut(ChannelHandlerContext ctx,Message message);

    //用于捕获IdleState#WRITER_IDLE事件（未在指定时间内向服务器发送数据），然后向Server发送一个心跳包
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        long time = new Date().getTime();
        sendMsg("客户端循环心跳监测发送: "+time, true);
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // write heartbeat to server
                ctx.writeAndFlush(Message.HEART_BEAT);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 由于chaquopy只能在Main App使用，所以执行器只好调用Main App
     * @param message
     * @param isLog
     */

    protected void sendMsg(String message, boolean isLog){
        android.os.Message msg = new android.os.Message();
        msg.obj = message;
        handler.sendMessage(msg);
        if (isLog){
            Log.d("WorkerHandler", message);
        }
    }

}
