package sdk;

import android.content.Intent;


import application.AppApplication;
import cn.com.brilliants.sdk.face.imageclient.ImageClient;
import cn.com.brilliants.sdk.face.imageclient.bean.CameraCaptureImagePushMessage;
import cn.com.brilliants.sdk.face.imageclient.impl.ImageClientActionListener;
import cn.com.brilliants.sdk.face.messageclient.constant.Global;
import io.netty.channel.Channel;
import tool.LogTools;

/**
 *
 * 监听抓拍信息TCP连接，注意一定要在控制TCP连接成功后才能连接成功,
 * 并且控制TCP连接一旦断开，该连接也会直接断开
 */
public class CarCameraImageUtil {
    private final String log_tag = "APITest";
    private String mark;
    private  ImageClient client;


    public void connectCarCameraImageTcp(String tag,String ip){
        if(tag==null) {
             tag = ip + "-" + Global.CAR_CAMERA_PICPUSH_PORT;
        }
        mark=tag;
        client=new ImageClient(mark,ip, new ImageClientActionListener() {
            @Override
            public void imageClinetConnectResult(Channel channel, String tag, int code) {
                //code  0-成功 1-连接失败 2-连接断开
                LogTools.println(log_tag, mark+"----------------相机抓拍TCP连接结果---------------+"+code);
            }

            @Override
            public void imageClinetReceiveData(Channel channel,String tag, CameraCaptureImagePushMessage msg) {
                LogTools.println(log_tag, mark+"----------------相机抓拍TCP连接收到数据---------------+车牌号："+msg.getCarMessage().getSzLprResult()+"--图片大小="+msg.getImageBytes().length);
                if(msg!=null) {
                    sendMessageToUI(msg);
                }
            }
        });
    }

    /**
     * 将信息通过广播发送给UI使用
     * @param msg
     */
    private void sendMessageToUI(CameraCaptureImagePushMessage msg) {
        Intent intent = new Intent();
        intent.setAction(AppApplication.PUBLIC_MESSAGEBROADCASE_NAME);
        intent.putExtra(AppApplication.TCP_PUSH_CARIMAGE_MSG, msg);
        AppApplication.context.sendBroadcast(intent);
    }
}
