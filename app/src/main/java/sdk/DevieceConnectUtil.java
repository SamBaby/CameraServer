package sdk;


import android.content.Intent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import application.AppApplication;
import bean.TcpMessage;
import cn.com.brilliants.sdk.face.messageclient.TcpClient;
import cn.com.brilliants.sdk.face.messageclient.TcpClientConfig;
import cn.com.brilliants.sdk.face.messageclient.TcpClientContext;
import cn.com.brilliants.sdk.face.messageclient.bean.JsonRequest;
import cn.com.brilliants.sdk.face.messageclient.bean.Packet;
import cn.com.brilliants.sdk.face.messageclient.constant.Global;
import cn.com.brilliants.sdk.face.messageclient.constant.MsgId;
import cn.com.brilliants.sdk.face.messageclient.constant.MsgType;
import cn.com.brilliants.sdk.face.messageclient.constant.PacketConstant;
import cn.com.brilliants.sdk.face.messageclient.event.ConnectionListener;
import cn.com.brilliants.sdk.face.messageclient.event.PushRecordListener;
import cn.com.brilliants.sdk.face.messageclient.event.ResponseListener;
import io.netty.channel.Channel;
import tool.LogTools;


/**
 * 控制TCP连接，连接车牌相机，控制车牌相机的消息交互；
 */
public class DevieceConnectUtil implements ConnectionListener, PushRecordListener {
    public int SDK_DEVICECONNECT_STATE = 0;//设备连接状况 -1-连接失败 0-初始未连接 100-正在连接中  200-连接成功（可以互相通讯）
    private final String log_tag = "APITest";
    private String mark;
    private final int MR_SENDDATA_OUTTIME = 8;//默认发送数据超时时间----秒
    private String connectIp;//当前连接的设备IP地址
    private TcpClient tcpClient;
    private TcpClientContext tcpClientContext;
    private List<PushRecordListener> pushRecordListenerList;


    /**
     * 获取设备连接状态
     *
     * @return
     */
    public boolean getDeviceConnectState() {
        if (SDK_DEVICECONNECT_STATE == 200 && connectIp != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加推送监听,注意在不用时一定要调用removePushRecordListener(),防止内存泄漏
     *
     * @param pl
     */
    public void addPushRecordListener(PushRecordListener pl) {
        if (pushRecordListenerList == null) {
            pushRecordListenerList = new ArrayList<>();
        }
        pushRecordListenerList.add(pl);
    }

    /**
     * 移除推送监听
     *
     * @param pl
     */
    public void removePushRecordListener(PushRecordListener pl) {
        if (pl == null || pushRecordListenerList == null || pushRecordListenerList.isEmpty()) {
            return;
        }
        String name = pl.getClass().getName();
        Iterator<PushRecordListener> iter = pushRecordListenerList.iterator();
        while (iter.hasNext()) {
            String s = iter.next().getClass().getName();
            if (s.equals(name)) {
                iter.remove();
                break;
            }
        }
    }

    /**
     * 获取请求序列号
     *
     * @return
     */
    public int getSequence() {
        if (tcpClientContext != null) {
            return tcpClientContext.sequence();
        } else {
            return -1;
        }
    }

    /**
     * 连接设备
     *
     * @param ip   设备IP地址
     *
     */
    public void connectDevice(String tag,String ip) {
        LogTools.println(log_tag, mark+ Global.eventLoopGroup);

        if (ip == null || Global.CAR_CAMERA_CONNECT_PORT == 0) {
            return;
        }
        if(tag==null){
            tag="ip:"+ip+"-端口:"+Global.CAR_CAMERA_CONNECT_PORT;
        }
        this.mark=tag;
        this.connectIp = ip;
        TcpClientConfig tcpClientConfig = TcpClientConfig.builder().host(ip).port(Global.CAR_CAMERA_CONNECT_PORT).connectTimeoutMillis(15000).build();
        tcpClient = new TcpClient(tcpClientConfig);
        try {
            SDK_DEVICECONNECT_STATE = 100;
            tcpClient.connect(this,mark);
        } catch (Exception e) {
            e.printStackTrace();
            connectIp = null;
            SDK_DEVICECONNECT_STATE = -1;
            LogTools.println(log_tag, mark+"连接失败" + e);
            sendMessageToUI(new TcpMessage(mark,201,"连接失败"));
        }
    }

    /**
     * 断开连接
     */
    public void disconnectDevice(){
        sendMessageToDevice(PacketConstant.MSG_DISCONNECT,8,null);
        closeDeviceConnect();
    }

    /**
     * 发送数据给设备
     *
     * @param request          请求体
     * @param timeout          超时时间----秒
     * @param responseListener 回调
     */
    public void sendMessageToDevice(Packet request, long timeout, ResponseListener responseListener) {
        if (tcpClientContext == null) {
            return;
        }
        tcpClientContext.send(request, timeout, TimeUnit.SECONDS, responseListener);
    }

    /**
     * 发送数据给设备
     *
     * @param request          请求体（采用默认超时时间）
     * @param responseListener 回调
     */
    public void sendMessageToDevice(Packet request, ResponseListener responseListener) {
        if (tcpClientContext == null) {
            return;
        }
        tcpClientContext.send(request, MR_SENDDATA_OUTTIME, TimeUnit.SECONDS, responseListener);
    }

    /**
     * 关闭设备连接
     */
    public void closeDeviceConnect() {
        if (tcpClient != null) {
            tcpClient.shutdownGracefully();
        }
        SDK_DEVICECONNECT_STATE = 0;
    }


    // 连接成功，连接成功只表示通道已打开，还不能与设备端交互，需要等待连接准备完成(ready)
    @Override
    public void success(Channel channel) {
      //  LogTools.println(log_tag, mark+"设备连接成功:" + channel.toString());
    }

    //连接失败
    @Override
    public void failed(Throwable throwable) {
        SDK_DEVICECONNECT_STATE = -1;
        LogTools.println(log_tag, mark+"设备连接失败!");
        if (throwable != null) {
            throwable.printStackTrace();
        }
        //失败时必须关闭TcpClient,然后可以选择重连
        tcpClient.shutdownGracefully();
        connectIp = null;
        sendMessageToUI(new TcpMessage(mark,201,"连接失败"));
    }

    //连接通道已准备就绪，拿到TcpClientContext，可以与设备进行交互了。
    @Override
    public void ready(TcpClientContext pTcpClientContext) {
        LogTools.println(log_tag,mark+"----------------服务器连接成功,可以读写数据----------------");
        tcpClientContext = pTcpClientContext;
        //设置推送监听
        tcpClientContext.setPushRecordListener(this);
        SDK_DEVICECONNECT_STATE = 200;
        sendMessageToUI(new TcpMessage(mark,100,"连接成功"));

    }

    //连接通道关闭时调用
    @Override
    public void close() {
        SDK_DEVICECONNECT_STATE = 0;
        LogTools.println(log_tag, mark+"设备连接关闭!");
        tcpClient.shutdownGracefully();
        sendMessageToUI(new TcpMessage(mark,202,"连接关闭"));
    }

    //接收到的数据，只建议做日志输出使用，不要用于业务处理
    @Override
    public void rx(Channel channel, Packet packet) {
        //去掉心跳
        if(packet.getUiMsgId()!= MsgId.MSG_C_HEART_BEAT) {
            LogTools.println(log_tag, mark+"收到=" + packet.getUiMsgId()+"--长度="+packet.getUiParaLength());
            TcpMessage msg=new TcpMessage(200,mark,packet,"接收到数据");
            sendMessageToUI(msg);
        }
    }

    //发送的数据，只建议做日志输出使用，不要用于业务处理
    @Override
    public void tx(Channel channel, Packet packet) {
        //去掉心跳
        if(packet.getUiMsgId()!= MsgId.MSG_C_HEART_BEAT) {
            LogTools.println(log_tag, mark+"发送=" + packet.getUiMsgId() + "--长度=" + packet.getUiParaLength());
        }
    }

    //收到的推送消息-----逐个发给添加监听的对象
    @Override
    public Packet received(Packet packet, JsonRequest jsonRequest) {
        //去掉心跳
        if(packet.getUiMsgId()!= MsgId.MSG_C_HEART_BEAT) {
          LogTools.println(log_tag,mark + "------收到推送消息：" + packet.toString(true));
        }
        if (pushRecordListenerList != null && !pushRecordListenerList.isEmpty()) {
            for (PushRecordListener impl : pushRecordListenerList) {
                impl.received(packet, jsonRequest);
            }
        } else {
            LogTools.println(log_tag,mark +"------暂时没有对象关心该消息:" + packet.toString(true));
        }
        //构建回复消息---否则无限推送
        Packet response = Packet.builder().uiMsgType(MsgType.RESPONSE).uiMsgId(packet.getUiMsgId()).uiAppPrivate(packet.getUiAppPrivate()).build();
        //返回应答
        return response;
    }


    /**
     * 将信息通过广播发送给UI使用
     * @param msg
     */
    private void sendMessageToUI(TcpMessage msg) {
        Intent intent = new Intent();
        intent.setAction(AppApplication.PUBLIC_MESSAGEBROADCASE_NAME);
        intent.putExtra(AppApplication.TCP_MSG_CONTROL, msg);
        AppApplication.context.sendBroadcast(intent);
    }
}
