package com.mrl.communicate.business;

public interface Executor {

    Object exec(String source, String funcName, Object[] params);

}
