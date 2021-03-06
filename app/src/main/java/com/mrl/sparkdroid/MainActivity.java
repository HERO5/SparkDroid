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

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.mrl.communicate.business.Executor;
import com.mrl.communicate.master.boot.TcpMaster;
import com.mrl.communicate.master.manager.JobManager;
import com.mrl.communicate.master.manager.JobManager2;
import com.mrl.communicate.worker.boot.TcpWorker;
import com.mrl.communicate.middle.ResultOfCall;
import com.mrl.sparkdroid.util.FileUtil;
import com.mrl.sparkdroid.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    private Button tensor;
    private Button tensorServer;
    private Button tensorClient;
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
        tensor = findViewById(R.id.tensor);
        tensorServer = findViewById(R.id.tensor_ps_server);
        tensorClient = findViewById(R.id.tensor_ps_client);
        master.setOnClickListener(mListener);
        worker.setOnClickListener(mListener);
        stop.setOnClickListener(mListener);
        tensor.setOnClickListener(mListener);
        tensorServer.setOnClickListener(mListener);
        tensorClient.setOnClickListener(mListener);
    }

    View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            String i = ip.getText().toString().trim();
            String p = port.getText().toString().trim();
            final String sourceCode = source.getText().toString();
            final long start = new Date().getTime();
            switch (view.getId()) {
                case R.id.master:
                    msgMaster.append("先清除已存在连接\n");
                    for(TcpWorker worker : tcpClients){
                        worker.shutDown();
                    }
                    tcpClients.clear();
                    TcpMaster.getInstance().shutDown();
                    //检查各项输入是否合法
                    if(!StringUtil.regxPort(p)){
                        Toast.makeText(MainActivity.this, "非法端口", Toast.LENGTH_LONG).show();
                        break;
                    }
                    if(sourceCode!=null && sourceCode.contains("def main") && sourceCode.contains("return")){
//                        sourceCode = source.getText().toString();
                        msgMaster.append("合法代码");
                    }else {
                        msgMaster.append("必须包含一个带单参、有返回值、名为main的方法");
                        break;
                    }
                    //检查完毕
//                    Intent intent1 = new Intent(MainActivity.this, ServerService.class);
//                    startService(intent1);
                    if(!TcpMaster.getInstance().isInit()) {
                        TcpMaster.getInstance().init(handlerMaster, new JobManager2(sourceCode, new String[]{""}), Integer.valueOf(p));
                    }
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
                            ResultOfCall res = call1(source, funcName, params);
                            return res;
                        }
                    };
                    for(int in=0;in<1;in++){
                        TcpWorker tcpClient = new TcpWorker(handlerWorker, executor, i, Integer.valueOf(p));
                        tcpClients.add(tcpClient);
                        tcpClient.connect();
                    }
                    msgMaster.append("......client is  ready......\n");
                    Log.d("on worker clicked", "......Worker is  ready......");
                    break;
                case R.id.stop:
                    for(TcpWorker worker : tcpClients){
                        worker.shutDown();
                    }
                    tcpClients.clear();
//                    Intent intent2 = new Intent(MainActivity.this, ServerService.class);
//                    stopService(intent2);
                    TcpMaster.getInstance().shutDown();
                    msgMaster.setText(JobManager.reportTime());
                    msgWorker.setText(null);
                    Log.d("on stop clicked", "......stop all......");
                    break;
                case R.id.tensor:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message1 = new Message();
                            message1.obj = "开始训练MNIST";
                            handlerMaster.sendMessage(message1);
                            PythonTest.testTensor();
                            long end = new Date().getTime();
                            Message message2 = new Message();
                            message2.obj = "MNIST训练完成\n模型保存在\"/storage/emulated/0/tensor-model/\"\n耗时:"+(end-start);
                            handlerMaster.sendMessage(message2);
                        }
                    }).start();
                    break;
                case R.id.tensor_ps_server:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message1 = new Message();
                            message1.obj = "开启 Tensor Server";
                            handlerMaster.sendMessage(message1);
//                            PythonTest.testTensorServer();
                            PyObject res1 = call(sourceCode, "train", null);
                            msgMaster.append(res1.toString()+"\n");
                            scrollWorker.scrollTo(0,msgWorker.getBottom());
                        }
                    }).start();
                    break;
                case R.id.tensor_ps_client:
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Message message1 = new Message();
//                            message1.obj = "开启 Tensor Client";
//                            handlerMaster.sendMessage(message1);
//                            PythonTest.testTensorClient();
//                        }
//                    }).start();
                    PyObject res2 = call(sourceCode, "train", null);
                    msgMaster.append(res2.toString()+"\n");
                    scrollMaster.scrollTo(0,msgMaster.getBottom());
                    break;
                default:
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
        }
        return sb.toString();
    }

    // 初始化Python环境
    void initPython(){
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }

    PyObject call(String source, String funcName, Object[] params){
        Python py = Python.getInstance();
        PyObject res = py.getModule("executor").callAttr("call", source, funcName, params);
        return res;
    }

    ResultOfCall call1(String source, String funcName, Object[] params){
        Python py = Python.getInstance();
        PyObject res = py.getModule("executor").callAttr("call1", source, funcName, params);
        if(res!=null){
            return res.toJava(ResultOfCall.class);
        }else {
            return null;
        }
    }

}
