package com.example.cameraserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

import application.AppApplication;
import cn.com.brilliants.sdk.face.imageclient.ImageClient;
import cn.com.brilliants.sdk.face.imageclient.bean.CameraCaptureImagePushMessage;
import cn.com.brilliants.sdk.face.imageclient.impl.ImageClientActionListener;
import cn.com.brilliants.sdk.face.messageclient.bean.body.CameraMessage485PushInBody;
import cn.com.brilliants.sdk.face.messageclient.constant.Global;
import cn.com.brilliants.sdk.face.messageclient.constant.MsgId;
import cn.com.brilliants.sdk.face.messageclient.event.PushRecordListener;
import cn.com.brilliants.sdk.face.receive.bean.TcpMessage;
import io.netty.channel.Channel;
import sdk.CarCameraImageUtil;
import sdk.DevieceConnectUtil;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    TextView port;
    TextView msg_car;
    private String connectIp;
    DevieceConnectUtil util;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = inetAddress.getHostAddress();
                        if (ip != null && ip.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+")) {
                            TextView txtIp = findViewById(R.id.text_ip);
                            txtIp.setText(ip);
                            connectIp = ip;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        port = findViewById(R.id.port);

        image = findViewById(R.id.image_car);
        msg_car = findViewById(R.id.text_car);
        Button connect = findViewById(R.id.connect);
        Button disconnect = findViewById(R.id.disconnect);

        startBroadcastListener();
        connect.setOnClickListener(v -> {
            if (server != null) {
                try {
                    server.close();
                    server = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!port.getText().toString().isEmpty()) {
                connectIp = port.getText().toString();
                connectDevice();
//                connectCarCameraImageTcp(connectIp);
            }
//            new ServerThread().start();

        });
        disconnect.setOnClickListener(v -> {
            if (server != null) {
                try {
                    server.close();
                    server = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(util != null){
                util.disconnectDevice();
            }
        });
    }

    private static String TAG = "ServerSocketTest";

    private ServerSocket server;
    Runnable conn = new Runnable() {
        public void run() {
            try {
                if (!port.getText().toString().isEmpty()) {
                    server = new ServerSocket(Integer.parseInt(port.getText().toString()));

                    while (true) {
                        Socket socket = server.accept();
                        if(socket.isConnected()){
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(socket.getInputStream()));

                            StringBuilder content = new StringBuilder();
                            String line;
                            while ((line = in.readLine()) != null) {
                                content.append(line);
                                content.append(System.lineSeparator());
                            }
                            runOnUiThread(() -> {
                                if (msg_car != null) {
                                    msg_car.setText("成功" + content.toString());
                                }
                            });
                            Log.i("response from server", content.toString());

                            in.close();
                            socket.close();
                        }
                    }
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (msg_car != null) {
                        msg_car.setText(e.getMessage());
                    }
                });
            }
        }
    };
    private MessageTransmitBroadCastReceiver receiver;
    private void startBroadcastListener() {
        //动态注册消息传递的广播
        receiver = new MessageTransmitBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppApplication.PUBLIC_MESSAGEBROADCASE_NAME);
        registerReceiver(receiver, intentFilter);
    }
    private void connectDevice(){
        util = new DevieceConnectUtil();
        util.connectDevice(null,connectIp);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //连接成功后开始发送请求
        if (!util.getDeviceConnectState()) {
            System.out.println("----------------连接车牌相机未连接---------------");
            msg_car.setText("----------------连接车牌相机未连接---------------");
        } else {
            //设置推送监听  包括监听485消息
//            util.addPushRecordListener(pushRecordListener);
            msg_car.setText("----------------连接车牌相机成功---------------");
        }
    }

    public PushRecordListener pushRecordListener = (packet, jsonRequest) -> {
        System.out.println("----前端收到推送消息了，可以处理--");
        if (packet == null || jsonRequest == null) {
            return null;
        }
        switch (packet.getUiMsgId()) {
            case MsgId.MSG_Q_READ_RS485_DATA://485消息
                if (jsonRequest.getData() instanceof CameraMessage485PushInBody) {
                    CameraMessage485PushInBody msg485 = (CameraMessage485PushInBody) jsonRequest.getData();
                    System.out.println("----收到了485消息--=" + msg485.getData());
                    msg_car.setText("----收到了485消息--=" + msg485.getData());
                }
                break;
        }
        return null;
    };
    public void connectCarCameraImageTcp(String ip) {
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
                msg_car.setText("----------------相机抓拍TCP连接收到数据---------------+车牌号：" + msg.getCarMessage().getSzLprResult() + "--图片大小=" + msg.getImageBytes().length);
//                SimpleDateFormat sd = new SimpleDateFormat("MM月DD日HH时mm分ss秒");
//                File file = new File("E:/testpic/" + sd.format(new Date()) + ".jpg");
//                try {
//                    FileOutputStream out = new FileOutputStream(file);
//                    out.write(msg.getImageBytes());
//                    out.flush();
//                    out.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                if (bitmap != null) {
                    image.setImageBitmap(null);
                    bitmap.recycle();
                }
                bitmap = BitmapFactory.decodeByteArray(msg.getImageBytes(), 0, msg.getImageBytes().length);
                image.setImageBitmap(bitmap);
            }
        });


    }

    private Bitmap bitmap;
    private class MessageTransmitBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            //收到控制类消息
            TcpMessage tcpMessage = (TcpMessage) intent.getSerializableExtra(AppApplication.TCP_MSG_CONTROL);
            if (tcpMessage != null) {
//                LogTools.println("test", "tcpMessage=" + tcpMessage.getCode() + "--" + tcpMessage.getErrHint());
                switch (tcpMessage.getCode()) {//99-地址解析异常  100-连接成功 200-收到设备发来的数据 201-连接失败 202-断开连接
                    case 201:
                    case 202:
                    case 203:
                        msg_car.setText(tcpMessage.getErrHint());
                        break;
                    case 100://连接成功  开启抓拍车牌监听连接  延迟几秒连接获取抓拍图片
                        msg_car.setText("连接成功");
                        handler.sendEmptyMessageDelayed(244, 4000);
                        break;
                }
                return;
            }
            //收到抓拍的车牌信息，包含图片
            CameraCaptureImagePushMessage imageMessage = (CameraCaptureImagePushMessage) intent.getSerializableExtra(AppApplication.TCP_PUSH_CARIMAGE_MSG);
            if (imageMessage != null) {
                msg_car.setText(imageMessage.getCarMessage().getSzLprResult());
                if (imageMessage.getImageBytes() != null) {
                    if (bitmap != null) {
                        image.setImageBitmap(null);
                        bitmap.recycle();
                    }
                    bitmap = BitmapFactory.decodeByteArray(imageMessage.getImageBytes(), 0, imageMessage.getImageBytes().length);
                    image.setImageBitmap(bitmap);
                }
            }

        }
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 244://开启抓拍图片获取tcp连接
                    connectCarCameraImage();
                    break;
            }
        }
    };
    private void connectCarCameraImage() {
        CarCameraImageUtil imageUtil = new CarCameraImageUtil();
        imageUtil.connectCarCameraImageTcp(null, connectIp);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) {
            try {
                server.close();
                server = null;
            } catch (IOException e) {
                if (msg_car != null) {
                    msg_car.setText(e.getMessage());
                }
            }
        }
    }

    private class ServerThread extends Thread {
        private static final String TAG = "ServerThread";
        private static final int SERVER_PORT = 8080;

        @Override
        public void run() {
            try {
                // 创建ServerSocket对象
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                Log.i(TAG, "Server listening on port " + SERVER_PORT + "...");

                while (true) {
                    // 等待客户端连接
                    Socket clientSocket = serverSocket.accept();
                    Log.i(TAG, "Connection established with " + clientSocket.getRemoteSocketAddress());

                    try {
                        // 初始化一个空字节数组
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[65535];
                        int bytesRead;

                        // 读取客户端发送的数据
                        InputStream inputStream = clientSocket.getInputStream();
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                            if (byteArrayOutputStream.toString().contains("}")) {
                                break;
                            }
                        }

                        // 解析JSON数据
                        String jsonString = byteArrayOutputStream.toString("UTF-8");
                        runOnUiThread(()->{
                            TextView v = findViewById(R.id.text_car);
                            v.setText(jsonString);
                        });
//                        JSONObject jsonObject = new JSONObject(jsonString);
//                        Log.i(TAG, "Received JSON data: " + jsonObject.toString());

//                        // 检查是否有图像数据
//                        if (jsonObject.has("imageFragmentFile")) {
//                            String base64ImageData = jsonObject.getString("imageFragmentFile").split(",")[1];
//                            byte[] imageData = Base64.decode(base64ImageData, Base64.DEFAULT);
//
//                            // 将字节数组转换为Bitmap
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
//
//                            // 在这里，你可以进一步处理图像，例如进行分析
//
//                            // 保存图像
//                            // 注意：由于 Android 的限制，无法在主线程中直接保存文件，请将此操作放在子线程中执行
//                            saveBitmapToFile(bitmap);
//                            Log.i(TAG, "Image saved.");
//                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Error decoding JSON data: " + e.getMessage());
                    }finally {
                        // 关闭客户端连接
                        clientSocket.close();
                    }
                }

            } catch (IOException e) {
                Log.e(TAG, "Error starting server: " + e.getMessage());
            }
        }

        private void saveBitmapToFile(Bitmap bitmap) {
            // 在这里实现将 Bitmap 保存到文件的逻辑
            // 请参考 Android 文档或其他资源了解如何保存 Bitmap 到文件
        }
    }
}