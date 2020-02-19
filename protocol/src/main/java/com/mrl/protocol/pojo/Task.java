package com.mrl.protocol.pojo;

import java.util.Arrays;
import java.util.Map;

/**
 * @program: com.mrl.netty.common.pojo
 * @description: 任务实体
 * @author:
 * @create: 2020-02-01 16:46
 **/
public class Task {

    public static final int FAILED = -1;
    public static final int SUCCESS = 1;

    private String id;
    private String name;
    private int state;
    private String func;
    private String funcName;
    private Object[] params;
    /**
     * complete用来存储本次处理得到的最终数据
     */
    private Object[] complete;
    /**
     * intermission用来存储本次处理得到的中间数据
     */
    private Object[] intermission;
    private String ops;
    private String describe;

    public Task(){}

    public Task(int state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Object[] getComplete() {
        return complete;
    }

    public void setComplete(Object[] complete) {
        this.complete = complete;
    }

    public Object[] getIntermission() {
        return intermission;
    }

    public void setIntermission(Object[] intermission) {
        this.intermission = intermission;
    }

    public String getOps() {
        return ops;
    }

    public void setOps(String ops) {
        this.ops = ops;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", func='" + func + '\'' +
                ", funcName='" + funcName + '\'' +
                ", params=" + Arrays.toString(params) +
                ", complete=" + Arrays.toString(complete) +
                ", intermission=" + Arrays.toString(intermission) +
                ", ops='" + ops + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }
}
