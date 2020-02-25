package com.mrl.communicate.master.handler;

import android.os.Handler;
import android.util.Log;

import com.mrl.communicate.master.manager.JobManager;
import com.mrl.protocol.pojo.Task;
import com.mrl.protocol.pojo.message.Message;
import com.mrl.protocol.pojo.message.MessageContent;
import com.mrl.protocol.pojo.message.MessageType;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @program: com.mrl.netty.server.handler
 * @description: manage the workers
 * @author:
 * @create: 2020-02-01 15:04
 **/
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private Handler handler;
    private JobManager jobManager;

    public ServerHandler(Handler handler, JobManager jobManager){
        this.handler = handler;
        this.jobManager = jobManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String workerId = getWorkerId(ctx);
        jobManager.addWorker(workerId, ctx);
        sendMsg("有新连接接入 " + workerId, true);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String workerId = getWorkerId(ctx);
        if(jobManager.removeWorker(workerId)){
            sendMsg("断开连接 " + workerId, true);
        }else {
            sendMsg("断开连接失败" + workerId, true);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String workerId = getWorkerId(ctx);
        Message message = (Message) msg;
        int type = message!=null?message.getMessageHeader().getMessageType():0;
        switch (type){
            case MessageType.TASK_GET:
                Task task = jobManager.getTask(workerId);
                sendMsg("来自"+workerId+"的任务请求", true);
                MessageContent<Task> content = new MessageContent<>(task);
                message.setMessageContent(content);
                message.getMessageHeader().setMessageType(MessageType.TASK_PUT);
                ctx.write(message);
                ctx.flush();
                break;
            case MessageType.TASK_SUBMIT:
                Object res = message.getMessageContent().getContent();
                boolean complete = jobManager.submitTask(workerId, res);
                sendMsg("来自"+workerId+"的提交; 结果正确："+complete+"\n", true);
                message.setMessageContent(null);
                message.getMessageHeader().setMessageType(MessageType.TASK_CONFIRM);
                ctx.write(message);
                ctx.flush();
                break;
            case MessageType.HEART_BEAT:
                sendMsg("来自客户端的心跳: "+ workerId,true);
                break;
            default:
                break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.READER_IDLE){
                String workerId = getWorkerId(ctx);
                sendMsg("关闭这个不活跃通道！"+workerId, true);
                ctx.channel().close();
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    private void sendMsg(String message, boolean isLog){
        android.os.Message msg = new android.os.Message();
        msg.obj = message;
        handler.sendMessage(msg);
        if (isLog){
            Log.d("ServerHandler", message);
        }
    }

    private String getWorkerId(ChannelHandlerContext ctx){
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();

        String workerIp = insocket.getAddress().getHostAddress();
        int port = insocket.getPort();
        return workerIp+":"+port;
    }
}
