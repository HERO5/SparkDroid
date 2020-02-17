package com.mrl.communicate.master.boot;

import android.os.Handler;

import com.mrl.communicate.master.handler.ServerHandler;
import com.mrl.communicate.parse.MessageDecoder;
import com.mrl.communicate.parse.MessageEncoder;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by user on 2016/10/27.
 */

public class TcpMaster {
    private ServerBootstrap mServerBootstrap;
    private EventLoopGroup mWorkerGroup;
    private ChannelFuture channelFuture;
    private boolean isInit;

    private static TcpMaster INSTANCE;

    public final static int PORT_NUMBER = 8888;

    private TcpMaster() {
    }

    public static TcpMaster getInstance() {
        if (INSTANCE == null) {
            synchronized (TcpMaster.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TcpMaster();
                }
            }
        }
        return INSTANCE;
    }

    public void init(final Handler handler, int port) {
        if (isInit) {
            return;
        }
        //创建worker线程池，这里只创建了一个线程池，使用的是netty的多线程模型
        mWorkerGroup = new NioEventLoopGroup();
        //服务端启动引导类，负责配置服务端信息
        mServerBootstrap = new ServerBootstrap();
        mServerBootstrap.group(mWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>(){
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS))
                                .addLast(new MessageEncoder())
                                .addLast(new MessageDecoder())
                                .addLast(new ServerHandler(handler));
                    }
                });

        channelFuture = mServerBootstrap.bind(port);
        isInit = true;
    }

    public void shutDown() {
        if (channelFuture != null && channelFuture.isSuccess()) {
            isInit = false;
            channelFuture.channel().closeFuture();
            mWorkerGroup.shutdownGracefully();
        }
    }
}
