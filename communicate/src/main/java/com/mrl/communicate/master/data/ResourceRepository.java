package com.mrl.communicate.master.data;

import com.mrl.protocol.pojo.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.netty.channel.ChannelHandlerContext;

/**
 * @program: com.mrl.netty.server.pojo
 * @description: hold all the alive channel
 * @author:
 * @create: 2020-02-01 14:58
 **/
public class ResourceRepository {

    //worker清单<workerIp, ctx>
    public static final Map<String, ChannelHandlerContext> workers = new ConcurrentHashMap<>();

    //未完成任务清单
    public static final List<Task> task = new CopyOnWriteArrayList<>();

    //任务分配清单<workerIp:port, task>
    public static final Map<String, Task> taskWorker = new ConcurrentHashMap<>();

    //注意，下面两种结果集不可混用
    //已完成任务清单<task.id, result> 适用于以task为单位的任务
    public static final Map<String, Object> taskResultMap = new ConcurrentHashMap<>();
    //已完成任务清单
    public static final List<Object> taskResultList = new ArrayList<>();

    public static long timeStart = -1;

    public static long timeStop = -1;
}
