package cn.mrlong.audiorecord;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
    // 实现分包 解决64k
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
