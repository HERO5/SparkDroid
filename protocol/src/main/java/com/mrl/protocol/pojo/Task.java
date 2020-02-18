package com.mrl.protocol.pojo;

import java.util.Map;

/**
 * @program: com.mrl.netty.common.pojo
 * @description: 任务实体
 * @author:
 * @create: 2020-02-01 16:46
 **/
public class Task {

    private String id;
    private String name;
    private int state;
    private String func;
    private String funcName;
    private Map<String, Object> params;
    private String ops;
    private String describe;

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

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
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
                "id=" + id +
                ", name=" + name +
                ", state=" + state +
                ", func='" + func + '\'' +
                ", funcName='" + funcName + '\'' +
                ", params=" + params +
                ", ops='" + ops + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }
}
