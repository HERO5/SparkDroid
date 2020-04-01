package com.mrl.sparkdroid;

import android.util.Log;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.util.ArrayList;
import java.util.List;

public class PythonTest {

    public static final String TAG = "PythonTest";

    public synchronized static void testTensor(){
        Python py = Python.getInstance();
        PyObject module = py.getModule("bp1");
        module.callAttr("train");
        module.callAttr("read_model");
    }

    public synchronized static void testTensorServer(){
        Python py = Python.getInstance();
        PyObject module = py.getModule("server_ps");
        module.callAttr("train", "192.168.1.6:2222",
                "192.168.1.12:2223,192.168.1.12:2224", "ps", 0);
    }

    public synchronized static String testTensorClient(){
        Python py = Python.getInstance();
        PyObject module = py.getModule("client_ps");
        PyObject res = module.callAttr("train");
        return res.toString();
    }

    // 调用python代码
    public static void testPythonCode(){
        Python py = Python.getInstance();
        PyObject module = py.getModule("hello");
        // 调用hello.py模块中的greet函数，并传一个参数
        // 等价用法：py.getModule("hello").get("greet").call("Android");
        module.callAttr("greet", "Android");

        // 调用python内建函数help()，输出了帮助信息
        Object oj = py.getBuiltins().get("help").call();
        System.out.println(oj);

        PyObject obj1 = module.callAttr("add", 2,3);
        // 将Python返回值换为Java中的Integer类型
        Integer sum = obj1.toJava(Integer.class);
        Log.d(TAG,"add = "+sum.toString());

        // 调用python函数，命名式传参，等同 sub(10,b=1,c=3)
        PyObject obj2 = module.callAttr("sub", 10,new Kwarg("b", 1), new Kwarg("c", 3));
        Integer result = obj2.toJava(Integer.class);
        Log.d(TAG,"sub = "+result.toString());

        // 调用Python函数，将返回的Python中的list转为Java的list
        PyObject obj3 = module.callAttr("get_list", 10,"xx",5.6,'c');
        List<PyObject> pyList = obj3.asList();
        Log.d(TAG,"get_list = "+pyList.toString());

        // 将Java的ArrayList对象传入Python中使用
        List<PyObject> params = new ArrayList<PyObject>();
        params.add(PyObject.fromJava("alex"));
        params.add(PyObject.fromJava("bruce"));
        module.callAttr("print_list", params);

        // Python中调用Java类
        PyObject obj4 = module.callAttr("get_java_bean");
        JavaBean data = obj4.toJava(JavaBean.class);
        data.print();

        module.callAttr("http_test");

        module.callAttr("numpy_test");

        module.callAttr("tensor_test");
    }
}
