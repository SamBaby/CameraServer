package application;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 * APP管理 提供全局设置处理  比如登录用户的信息 ，图片加载器，极光推送启动，极光推送别名设置等；
 */

public class AppApplication extends Application {

    public static Context context;
    /**
     * 全局数据广播
     */
    public final static String PUBLIC_MESSAGEBROADCASE_NAME = "com.qianyi.carcamer.app.message";

    /**
     * 控制类消息,该key中存放的为TcpMessage
     */
    public final static String TCP_MSG_CONTROL="message";
    /**
     * 收到推送过来的车牌抓拍消息，该key中存放的为CameraCaptureImagePushMessage
     */
    public final static String TCP_PUSH_CARIMAGE_MSG="get_car_image";
    /**
     * 已经连接的通道
     */
   // public final static Map<String, ChannelFuture> connectFuttre=new HashMap<>();

    /**
     * 图片处理的缓存路径
     */
    public static String imageCacheDir;
    /**
     * 全局使用线程池
     * 网络请求、耗时等线程操作均放入线程测操作
     */
    public static ExecutorService actionThreadPool = Executors.newFixedThreadPool(6);


    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        imageCacheDir = context.getExternalCacheDir().getAbsolutePath() + "/";
        initSet();
    }






    /**
     * 初始化设置
     */
    private void initSet() {
        //初始化图片加载
        Fresco.initialize(this);
        // 防止被系统字体改变默认得字体大小
        Resources resource = getResources();
        Configuration c = resource.getConfiguration();
        c.fontScale = 1.0f;
        resource.updateConfiguration(c, resource.getDisplayMetrics());

    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}
