package com.mrl.sparkdroid;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.mrl.communicate.business.Executor;
import com.mrl.communicate.master.boot.TcpMaster;
import com.mrl.communicate.master.manager.JobManager;
import com.mrl.communicate.worker.boot.TcpWorker;
import com.mrl.sparkdroid.util.FileUtil;
import com.mrl.sparkdroid.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static String CACHE_DIR;
    public static String FILE_DIR;
    public static String SD_DIR;

    public static final int MAX_BUFF = 0x1000;

    private ScrollView scrollMaster;
    private ScrollView scrollWorker;
    private TextView msgMaster;
    private TextView msgWorker;
    private EditText source;
    private EditText ip;
    private EditText port;
    private Button master;
    private Button worker;
    private Button stop;
    private final Handler handlerMaster = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msgMaster.getText().length() > MAX_BUFF) msgMaster.setText(null);
            msgMaster.append(msg.obj+"\n");
            scrollMaster.scrollTo(0,msgMaster.getBottom());
        }
    };
    private final Handler handlerWorker = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msgWorker.getText().length() > MAX_BUFF) msgWorker.setText(null);
            msgWorker.append(msg.obj+"\n");
            scrollWorker.scrollTo(0,msgWorker.getBottom());
        }
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private final List<TcpWorker> tcpClients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        init();
        initPython();
        testPythonCode();
    }

    //通过一个函数来申请权限
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        CACHE_DIR = this.getCacheDir().toString();
        FILE_DIR = this.getFilesDir().toString();
        SD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
        scrollMaster = findViewById(R.id.scroll_master);
        scrollWorker = findViewById(R.id.scroll_worker);
        msgMaster = findViewById(R.id.msg_master);
        msgWorker = findViewById(R.id.msg_worker);
        source = findViewById(R.id.source);
        ip = findViewById(R.id.ip);
        port = findViewById(R.id.port);
        master = findViewById(R.id.master);
        worker = findViewById(R.id.worker);
        stop = findViewById(R.id.stop);
        master.setOnClickListener(mListener);
        worker.setOnClickListener(mListener);
        stop.setOnClickListener(mListener);
    }

    View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            String i = ip.getText().toString().trim();
            String p = port.getText().toString().trim();
            switch (view.getId()) {
                case R.id.master:
                    msgMaster.append("先清除已存在连接\n");
                    for(TcpWorker worker : tcpClients){
                        worker.shutDown();
                    }
                    if(!StringUtil.regxPort(p)){
                        Toast.makeText(MainActivity.this, "非法端口", Toast.LENGTH_LONG).show();
                        break;
                    }
                    String sourceCode = source.getText().toString();
                    if(sourceCode!=null && sourceCode.contains("def main") && sourceCode.contains("return")){
                        sourceCode = source.getText().toString();
                    }else {
                        msgMaster.append("必须包含一个带单参、有返回值、名为main的方法");
                        break;
                    }
                    JobManager.initTask(10, sourceCode);
//                    Intent intent1 = new Intent(MainActivity.this, ServerService.class);
//                    startService(intent1);
                    TcpMaster.getInstance().init(handlerMaster, Integer.valueOf(p));
                    msgMaster.append("......Master is  ready......\n");
                    Log.d("on master click", "......Master is  ready......");
                    break;
                case R.id.worker:
                    Log.d("on worker clicked", "......Worker is  init......");
                    if(!StringUtil.regxIp(i) || !StringUtil.regxPort(p)) {
                        Toast.makeText(MainActivity.this, "非法IP/端口", Toast.LENGTH_LONG).show();
                        break;
                    }
                    Executor executor = new Executor() {
                        //这里加上@Override就会报错?
                        public Object exec(String source, String funcName, Object[] params) {
                            Object res = call(source, funcName, params);
                            return res.toString();
                        }
                    };
                    TcpWorker tcpClient = new TcpWorker(handlerWorker, executor, i, Integer.valueOf(p));
                    tcpClients.add(tcpClient);
                    tcpClient.connect();
                    msgMaster.append("......client is  ready......\n");
                    Log.d("on worker clicked", "......Worker is  ready......");
                    break;
                case R.id.stop:
                    for(TcpWorker worker : tcpClients){
                        worker.shutDown();
                    }
//                    Intent intent2 = new Intent(MainActivity.this, ServerService.class);
//                    stopService(intent2);
                    TcpMaster.getInstance().shutDown();
                    String files = reportFiles();
                    msgMaster.setText(files);
                    msgWorker.setText(null);
                    Log.d("on stop clicked", "......stop all......");
                    break;
            }
        }
    };

    public String reportFiles(){
        StringBuilder sb = new StringBuilder();
        ArrayList<String> listFileName = new ArrayList<String>();
        sb.append(FILE_DIR+":\n");
        FileUtil.getAllFileName(FILE_DIR, listFileName);
        Collections.sort(listFileName);
        for(String name : listFileName){
            sb.append(name+"\n");
//            if(name.endsWith(".py")){
//                FileUtil.delFile(name);
//            }
        }
        return sb.toString();
    }

    // 初始化Python环境
    void initPython(){
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }

    Object call(String source, String funcName, Object[] params){
        Python py = Python.getInstance();
        Object res = py.getModule("executor").callAttr("call", source, funcName, params);
        return res;
    }

    // 调用python代码
    void testPythonCode(){
        Python py = Python.getInstance();
        // 调用hello.py模块中的greet函数，并传一个参数
        // 等价用法：py.getModule("hello").get("greet").call("Android");
        py.getModule("hello").callAttr("greet", "Android");

        // 调用python内建函数help()，输出了帮助信息
        Object oj = py.getBuiltins().get("help").call();
        System.out.println(oj);

        PyObject obj1 = py.getModule("hello").callAttr("add", 2,3);
        // 将Python返回值换为Java中的Integer类型
        Integer sum = obj1.toJava(Integer.class);
        Log.d(TAG,"add = "+sum.toString());

        // 调用python函数，命名式传参，等同 sub(10,b=1,c=3)
        PyObject obj2 = py.getModule("hello").callAttr("sub", 10,new Kwarg("b", 1), new Kwarg("c", 3));
        Integer result = obj2.toJava(Integer.class);
        Log.d(TAG,"sub = "+result.toString());

        // 调用Python函数，将返回的Python中的list转为Java的list
        PyObject obj3 = py.getModule("hello").callAttr("get_list", 10,"xx",5.6,'c');
        List<PyObject> pyList = obj3.asList();
        Log.d(TAG,"get_list = "+pyList.toString());

        // 将Java的ArrayList对象传入Python中使用
        List<PyObject> params = new ArrayList<PyObject>();
        params.add(PyObject.fromJava("alex"));
        params.add(PyObject.fromJava("bruce"));
        py.getModule("hello").callAttr("print_list", params);

        // Python中调用Java类
        PyObject obj4 = py.getModule("hello").callAttr("get_java_bean");
        JavaBean data = obj4.toJava(JavaBean.class);
        data.print();
    }

}
