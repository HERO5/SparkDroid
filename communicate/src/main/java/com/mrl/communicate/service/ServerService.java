package com.mrl.communicate.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.mrl.communicate.master.boot.TcpMaster;

public class ServerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        TestServer.getInstance().init();
//        TcpMaster.getInstance().init();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        TestServer.getInstance().shutDown();
//        TcpMaster.getInstance().shutDown();
    }

}
