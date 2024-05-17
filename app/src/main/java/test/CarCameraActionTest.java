package test;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.brilliants.sdk.face.imageclient.ImageClient;
import cn.com.brilliants.sdk.face.imageclient.bean.CameraCaptureImagePushMessage;
import cn.com.brilliants.sdk.face.imageclient.impl.ImageClientActionListener;
import cn.com.brilliants.sdk.face.messageclient.bean.JsonRequest;
import cn.com.brilliants.sdk.face.messageclient.bean.JsonResponse;
import cn.com.brilliants.sdk.face.messageclient.bean.Packet;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraEncryptionSetInBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraGetWhiteListFileInBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraMessage485PushInBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraNetQueryInBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraNetQueryOutBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraNetSetupInBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraQueryWhiteByNumActionInBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraQueryWhiteByNumActionOutBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraSetMessage485InBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraTimeGetInBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraTimeGetOutBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraTimeSetupInBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraVersionMessageInBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraVersionMessageOutBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraWhiteListActionInBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraWhiteListDeleteInBody;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CarBrand_WhiteList;
import cn.com.brilliants.sdk.face.messageclient.constant.Global;
import cn.com.brilliants.sdk.face.messageclient.constant.MsgId;
import cn.com.brilliants.sdk.face.messageclient.constant.PacketConstant;
import cn.com.brilliants.sdk.face.messageclient.event.PushRecordListener;
import cn.com.brilliants.sdk.face.messageclient.event.ResponseListener;
import cn.com.brilliants.sdk.face.receive.ReceiveServer;
import cn.com.brilliants.sdk.face.receive.bean.TcpMessage;
import cn.com.brilliants.sdk.face.receive.event.PushReceiveMessageListener;
import cn.com.brilliants.sdk.face.receive.util.DataActionUtil;
import io.netty.channel.Channel;
import sdk.DevieceConnectUtil;

/**
 * 基础设置-日期时间，补光灯，设备音量，屏幕保护，设备参数等设置；
 */
public class CarCameraActionTest {
    private static String ip = "192.168.1.207";
    private static int receiveDataPort = 30600;
    private static DevieceConnectUtil util;


    public static void main(String[] args) {
        System.out.println("----------------开始调试---------------");
        //开启log调试
        Global.IS_DEBUG = true;
        //先开启数据监听服务器
        //startReceiveServer(receiveDataPort);
        //连接车牌相机
        connectCarCameraDevice1(ip);
        try {
            Thread.sleep(3000);
            //System.out.println("----------------开启车牌抓拍监听---------------");
            // connectCarCameraImageTcp(ip);//开启车牌抓拍监听
            //----------------------已完成调试------------
            // getCamerTime();//获取相机日期和时间
            //setCamerTime();//测试设置日期和时间
            //getCamerNetwork();//查询相机网络
            // setCamerNetwork();//测试设置相机网络
            // setEncryption();//设置加密
            //setData485();//发送485透传消息
            //getData485();//获取485消息
            //addWhiteList();//添加白名单
            // getWhiteMsgByCarNum("ABC123");//通过车牌号码查询白名单信息
            //deleteAllWhiteList();//删除全部白名单
            //deleteWhiteList();//删除白名单
            //getCamerVersionMessage();//查询相机版本信息
            // Thread.sleep(3000);
            System.out.println("------%%%%------执行测试程序--------%%%%%------");
            getWhiteListFile();//相机获取白名单文件
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加白名单
     */
    public static void addWhiteList() {
        int sequence = util.getSequence();
        CameraWhiteListActionInBody cwla = new CameraWhiteListActionInBody();
        cwla.setUcType((byte) 0);
        List<CarBrand_WhiteList> list = new ArrayList<>();
        CarBrand_WhiteList wl = new CarBrand_WhiteList();
        wl.setCarBrandNum("ABC123");
        wl.setStartTime("20210112190303");
        wl.setEndTime("20211012190303");
        list.add(wl);
        CarBrand_WhiteList wl2 = new CarBrand_WhiteList();
        wl2.setCarBrandNum("ABC456");
        wl2.setStartTime("20210212190310");
        wl2.setEndTime("20211112190330");
        list.add(wl2);
        cwla.setWhiteLists(list);
        Packet paket = PacketConstant.create(cwla, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----添加白名单结果--=" + jsonResponse.getResponseCode());
            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----添加白名单超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----添加白名单failed--=");
            }
        });
    }


    /**
     * 开启接收数据端口，完成后可以关闭掉该端口
     */
    public static void startReceiveServer(int port) {
        try {
            //注意。通过该回调的数据不在UI线程中，无法直接使用
            ReceiveServer.getInstance().setPushReceiveMessageListener(new PushReceiveMessageListener() {
                @Override
                public void receivedMessage(TcpMessage msg) {
                    if (msg == null) {
                        return;
                    }
                    System.out.println("收到推送消息了。。。。。。。。。。。。。。。。。。。。。。。。。。。消息code="+msg.getCode());
                    switch (msg.getCode()) {//99-地址解析异常  100-连接成功 200-正常 201-连接失败 202-断开连接 203-心跳响应超时
                        case 200://连接正常 收到数据
                            //收到白名单文件csv文件
                            if (msg.getBytes() != null) {
                                List<CarBrand_WhiteList> list = DataActionUtil.getCarBrandWhiteListFromBytes(msg.getBytes());
                                if (list != null) {
                                    System.out.println("收到推送白名单数量=" + list.size());
                                }
                            }
                            break;
                    }
                    //关闭该接收端口
                    ReceiveServer.getInstance().closeServer();
                }
            });
            ReceiveServer.getInstance().startServer(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除白名单
     */
    public static void deleteWhiteList() {
        int sequence = util.getSequence();
        CameraWhiteListActionInBody cwla = new CameraWhiteListActionInBody();
        cwla.setUcType((byte) 1);
        List<CarBrand_WhiteList> list = new ArrayList<>();
        CarBrand_WhiteList wl1 = new CarBrand_WhiteList();
        wl1.setCarBrandNum("ABC123");
        wl1.setStartTime("20210112190303");
        wl1.setEndTime("20211012190303");
        list.add(wl1);
        CarBrand_WhiteList wl2 = new CarBrand_WhiteList();
        wl2.setCarBrandNum("ABC456");
        wl2.setStartTime("20210212190310");
        wl2.setEndTime("20211112190330");
        list.add(wl2);
        cwla.setWhiteLists(list);
        Packet paket = PacketConstant.create(cwla, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----删除白名单结果--=" + jsonResponse.getResponseCode());
            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----删除白名单超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----删除白名单failed--=");
            }
        });
    }

    /**
     * 删除全部白名单
     */
    public static void deleteAllWhiteList() {
        int sequence = util.getSequence();
        CameraWhiteListDeleteInBody cwla = new CameraWhiteListDeleteInBody();
        Packet paket = PacketConstant.create(cwla, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----删除全部白名单结果--=" + jsonResponse.getResponseCode());
            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----删除全部白名单超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----删除全部白名单failed--=");
            }
        });
    }

    /**
     * 通过车牌号码查询白名单信息
     */
    public static void getWhiteMsgByCarNum(String num) {
        int sequence = util.getSequence();
        CameraQueryWhiteByNumActionInBody cwla = new CameraQueryWhiteByNumActionInBody();
        cwla.setCarNum(num);
        Packet paket = PacketConstant.create(cwla, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----通过车牌号码查询白名单信息结果响应--=" + jsonResponse.getResponseCode());
                if (jsonResponse.getData() instanceof CameraQueryWhiteByNumActionOutBody) {
                    CameraQueryWhiteByNumActionOutBody out = (CameraQueryWhiteByNumActionOutBody) jsonResponse.getData();
                    System.out.println("----通过车牌号码查询白名单信息结果--=" + out.getUcResult());
                    if (out.getCarMessage() != null) {
                        System.out.println("----通过车牌号码查询白名单信息结果--=" + out.getCarMessage().getCarBrandNum());
                        System.out.println("----通过车牌号码查询白名单信息结果--=" + out.getCarMessage().getStartTime());
                    }
                }
            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----通过车牌号码查询白名单信息超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----通过车牌号码查询白名单信息failed--=");
            }
        });
    }

    /**
     * 查询相机版本信息
     */
    public static void getCamerVersionMessage() {
        int sequence = util.getSequence();
        CameraVersionMessageInBody net = new CameraVersionMessageInBody();
        System.out.println("----查询相机版本信息--+" + sequence);
        Packet paket = PacketConstant.create(net, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----查询相机版本信息结果--=" + jsonResponse.getResponseCode());
                if (jsonResponse.getData() instanceof CameraVersionMessageOutBody) {
                    CameraVersionMessageOutBody out = (CameraVersionMessageOutBody) jsonResponse.getData();
                    System.out.println("产品序列号=" + out.getSzSerialNum());

                }
            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----查询相机版本信息超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----查询相机版本信息failed--=");
            }
        });
    }

    /**
     * 相机获取白名单文件,CSV文件
     * 先保证开启了端口接收相机推送数据，然后再调用该方法发出指令，相机会在稍后主动连接端口，主动推送过来
     * 开启端口参见
     * startReceiveServer(receiveDataPort)
     * 接收到的文件格式CSV文件，内容大概是这样的
     * &车牌号码@起始有效期$结束有效期
     */
    public static void getWhiteListFile() {
        startReceiveServer(receiveDataPort);
        //等待端口开启
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int sequence = util.getSequence();
        CameraGetWhiteListFileInBody net = new CameraGetWhiteListFileInBody();
        Packet paket = PacketConstant.create(net, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----相机获取白名单文件接口调用情况--=" + jsonResponse.getResponseCode());
                if (jsonResponse.getData() instanceof CameraVersionMessageOutBody) {
                    CameraVersionMessageOutBody out = (CameraVersionMessageOutBody) jsonResponse.getData();
                    System.out.println("相机获取白名单文件响应=" + out.getSzKernelVersion());
                }
            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----相机获取白名单文件超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----相机获取白名单文件failed--=");
            }
        });
    }

    /**
     * 查询相机网络
     */
    public static void getCamerNetwork() {
        int sequence = util.getSequence();
        CameraNetQueryInBody net = new CameraNetQueryInBody();
        System.out.println("----查询相机网络--");
        Packet paket = PacketConstant.create(net, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----查询相机网络结果--=" + jsonResponse.getResponseCode());
                if (jsonResponse.getData() instanceof CameraNetQueryOutBody) {
                    CameraNetQueryOutBody out = (CameraNetQueryOutBody) jsonResponse.getData();
                    System.out.println("相机IP=" + out.getUiIPAddress());
                    System.out.println("相机子网掩码=" + out.getUiMaskAddress());
                    System.out.println("相机网关=" + out.getUiGatewayAddress());
                    System.out.println("相机DNS1=" + out.getUiDNS1());
                }
            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----查询相机网络超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----查询相机网络failed--=");
            }
        });
    }

    /**
     * 发送485透传消息
     */
    public static void setData485() {
        int sequence = util.getSequence();
        CameraSetMessage485InBody net = new CameraSetMessage485InBody();
        net.setData("abcdehhh");
        Packet paket = PacketConstant.create(net, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----发送485消息结果--=" + jsonResponse.getResponseCode());
            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----发送485消息超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----发送485消息failed--=");
            }
        });
    }

    /**
     * 获取485消息，添加推送简单即可
     */
    public static void getData485() {
        //连接时如果添加了该监听，就可以不用添加了
        util.addPushRecordListener(pushRecordListener);
    }

    /**
     * 设置相机网络
     */
    public static void setCamerNetwork() {
        int sequence = util.getSequence();
        CameraNetSetupInBody net = new CameraNetSetupInBody();
        net.setUiIPAddress("192.168.1.128");
        net.setUiMaskAddress("255.255.255.0");
        net.setUiGatewayAddress("192.168.1.1");
        net.setUiDNS1("192.168.1.3");
        Packet paket = PacketConstant.create(net, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----设置相机网络结果--=" + jsonResponse.getResponseCode());
            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----设置相机网络超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----设置相机网络failed--=");
            }
        });
    }


    /**
     * 设置相机时间
     */
    public static void setCamerTime() {
        int sequence = util.getSequence();
        CameraTimeSetupInBody time = new CameraTimeSetupInBody();
        time.setUsYear(2019);
        time.setUcMonth(7);
        time.setUcDay(2);
        time.setUcHour(12);
        time.setUcMinute(5);
        time.setUcSecond(23);
        time.setUcDayFmt(0);

        Packet paket = PacketConstant.create(time, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----相机时间设置结果--=" + jsonResponse.getResponseCode());
            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----相机时间设置超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----相机时间设置结果failed--=");
            }
        });
    }

    /**
     * 获取相机时间
     */
    public static void getCamerTime() {
        int sequence = util.getSequence();
        CameraTimeGetInBody time = new CameraTimeGetInBody();

        Packet paket = PacketConstant.create(time, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----获取相机时间结果--=" + jsonResponse.getResponseCode());
                if (jsonResponse.getData() instanceof CameraTimeGetOutBody) {
                    CameraTimeGetOutBody time = (CameraTimeGetOutBody) jsonResponse.getData();
                    System.out.println("----获取相机时间结果--=" + time.getUsYear() + "年" + time.getUcMonth() + "月" +
                            time.getUcDay() + "日" + time.getUcHour() + "时" + time.getUcMinute() + "分" + time.getUcSecond() + "秒");
                }
            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----获取相机时间超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----获取相机时间结果failed--=");
            }
        });
    }

    /**
     * 设置二次加密
     */
    public static void setEncryption() {
        int sequence = util.getSequence();
        CameraEncryptionSetInBody net = new CameraEncryptionSetInBody();
        Packet paket = PacketConstant.create(net, sequence);
        util.sendMessageToDevice(paket, new ResponseListener() {
            @Override
            public void received(Packet response, JsonResponse jsonResponse) {
                System.out.println("----二次加密结果--=" + jsonResponse.getResponseCode());

            }

            @Override
            public void timeout(int msgId, int sequence) {
                System.out.println("----二次加密超时--=");
            }

            @Override
            public void failed(int msgId, int sequence, Channel channel) {
                System.out.println("----二次加密failed--=");
            }
        });
    }

    /**
     * 连接车牌相机通讯TCP
     */
    public static void connectCarCameraDevice1(String ip) {
        System.out.println("----------------开始连接车牌相机---------------");
        util = new DevieceConnectUtil();
        util.connectDevice(null,ip);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //连接成功后开始发送请求
        if (!util.getDeviceConnectState()) {
            System.out.println("----------------连接车牌相机未连接---------------");
            return;
        } else {
            //设置推送监听  包括监听485消息
            util.addPushRecordListener(pushRecordListener);
        }
        //断开与设备的连接
        // util.disconnectDevice();


    }

    /**
     * 连接车牌相机获取抓拍图片TCP
     */
    public static void connectCarCameraImageTcp(String ip) {
        System.out.println("----------------开始连接车牌相机抓拍TCP---------------");
        String tag = ip + "-" + Global.CAR_CAMERA_PICPUSH_PORT;

        ImageClient client = new ImageClient(tag, ip, new ImageClientActionListener() {
            @Override
            public void imageClinetConnectResult(Channel channel, String tag, int code) {
                System.out.println("----------------相机抓拍TCP连接结果---------------+" + code);
            }

            @Override
            public void imageClinetReceiveData(Channel channel, String tag, CameraCaptureImagePushMessage msg) {
                System.out.println("----------------相机抓拍TCP连接收到数据---------------+车牌号：" + msg.getCarMessage().getSzLprResult() + "--图片大小=" + msg.getImageBytes().length);
                SimpleDateFormat sd = new SimpleDateFormat("MM月DD日HH时mm分ss秒");
                File file = new File("E:/testpic/" + sd.format(new Date()) + ".jpg");
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(msg.getImageBytes());
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


    /**
     * 接收相机主动推送的消息
     */
    public static PushRecordListener pushRecordListener = new PushRecordListener() {
        @Override
        public Packet received(Packet packet, JsonRequest jsonRequest) {
            System.out.println("----前端收到推送消息了，可以处理--");
            if (packet == null || jsonRequest == null) {
                return null;
            }
            switch (packet.getUiMsgId()) {
                case MsgId.MSG_Q_READ_RS485_DATA://485消息
                    if (jsonRequest.getData() instanceof CameraMessage485PushInBody) {
                        CameraMessage485PushInBody msg485 = (CameraMessage485PushInBody) jsonRequest.getData();
                        System.out.println("----收到了485消息--=" + msg485.getData());
                    }
                    break;
            }
            return null;
        }
    };


}
