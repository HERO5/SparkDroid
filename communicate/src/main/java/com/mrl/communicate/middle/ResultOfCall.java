package com.mrl.communicate.middle;

import com.mrl.protocol.pojo.Task;

/**
 * @descripte 调用python后的返回值，其中type和MessageHead中的messageType对应
 *  data是处理结果，类型为TASK_SUBMIT说明这是一个最终结果，INTERMISSION说明这是一个未完成的结果
 *  此类放在这里的意义在于，app模块不能直接调用protocol模块的Task，因此需要一个过渡
 * @author mrl
 */
public class ResultOfCall {

    private int state;
    private Object[] complete;

    private Object[] intermission;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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
}
