package com.mrl.communicate.worker.boot;

import android.os.Handler;

import com.mrl.communicate.business.Executor;
import com.mrl.communicate.worker.handler.WorkerHandler;
import com.mrl.communicate.parse.MessageDecoder;
import com.mrl.communicate.parse.MessageEncoder;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @program: com.mrl.netty.client.boot
 * @description:
 * @author:
 * @create: 2020-02-01 18:51
 **/
public class TcpWorker {

    private String host;
    private int port;
    private Bootstrap bootstrap;
    EventLoopGroup group;
    /** 将Channel保存起来, 可用于在其他非handler的地方发送数据 */
    private ChannelFuture channelFuture;

    public TcpWorker(Handler handler, Executor executor, String host, int port) {
        this.host = host;
        this.port = port;
        init(handler, executor);
    }

    private void init(final Handler handler, final Executor executor) {
        group = new NioEventLoopGroup();
        // bootstrap 可重用, 只需在TcpClient实例化的时候初始化即可.
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>(){
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0,4,0, TimeUnit.SECONDS))
                                .addLast(new MessageEncoder())
                                .addLast(new MessageDecoder())
                                .addLast(new WorkerHandler(handler, executor));
                    }
                });
    }

    /**
     * 向远程TCP服务器请求连接
     */
    public void connect() {
        synchronized (bootstrap) {
            channelFuture= bootstrap.connect(host, port);
            channelFuture.addListener(getConnectionListener());
        }
    }

    private ChannelFutureListener getConnectionListener() {
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    future.channel().pipeline().fireChannelInactive();
                }
            }
        };
    }

    public void shutDown() {
        if (channelFuture != null && channelFuture.isSuccess()) {
            channelFuture.channel().closeFuture();
            group.shutdownGracefully();
        }
    }

}
