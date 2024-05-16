package com.example.cameraserver;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

import Util.UtilString;
import json.ResponseAlarmInfoPlate;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    TextView port;
    TextView msg_car;
    private String connectIp;

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
        port.setText("8080");
        image = findViewById(R.id.image_car);
        msg_car = findViewById(R.id.text_car);
        Button connect = findViewById(R.id.connect);
        Button disconnect = findViewById(R.id.disconnect);

        connect.setOnClickListener(v -> {
            if (server != null) {
                try {
                    server.close();
                    server = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
//                HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
//                server.createContext("/", new MyHandler());
////                server.setExecutor(null); // creates a default executor
//                server.start();
            } catch (Exception e) {
                Log.e("SUNServer", e.toString());
            }

            new Thread(()->{
                try {
                    // Create server socket
                    server = new ServerSocket(8080);

                    while (true) {
                        // Accept incoming connections
                        Socket socket = server.accept();

                        // Handle client request in a separate thread
                        new Thread(() -> handleClient(socket)).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

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
        });
    }

    private static String TAG = "ServerSocketTest";

    private ServerSocket server;
    private static void handleClient(Socket socket) {
        try {
            // Create input and output streams
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Read message from client
            String message = dataInputStream.readUTF();

            // Process message (e.g., perform some action or computation)
            // For this example, just echo back the message
            String response = "Server received: " + message;

            // Send response back to client
            dataOutputStream.writeUTF(checkType(""));
            dataOutputStream.flush();

            // Close streams and socket
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String checkType(String input) {
            if (input != null && input.contains("{")) {
                input = input.substring(input.indexOf("{"));
                try {
                    JSONObject obj = new JSONObject(input);
                    if (!obj.isNull("AlarmInfoPlate")) {
                        ResponseAlarmInfoPlate res = new ResponseAlarmInfoPlate();
                        res.setInfo("ok");
                        res.setContent("retransfer_stop");
                        res.setIs_pay("true");
                        res.setSerialData(new ArrayList<>());
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        JsonObject ret  = new JsonObject();
                        ret.add(UtilString.CameraString.Response_AlarmInfoPlate, gson.toJsonTree(res));
                        return gson.toJson(ret);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
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

    private void saveBitmapToFile(Bitmap bitmap) {
        // 在这里实现将 Bitmap 保存到文件的逻辑
        // 请参考 Android 文档或其他资源了解如何保存 Bitmap 到文件
    }
}

class MyHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        InputStream is = t.getRequestBody();
        byte[] buffer = new byte[65535];
        int bytesRead;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 读取客户端发送的数据
        while ((bytesRead = is.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        String response = checkType("");
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String checkType(String input) {
        if (input != null && input.contains("{")) {
            input = input.substring(input.indexOf("{"));
            try {
                JSONObject obj = new JSONObject(input);
                if (!obj.isNull("AlarmInfoPlate")) {
                    ResponseAlarmInfoPlate res = new ResponseAlarmInfoPlate();
                    res.setInfo("ok");
                    res.setContent("retransfer_stop");
                    res.setIs_pay("true");
                    res.setSerialData(new ArrayList<>());
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    return gson.toJson(res);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String cameraContent() {
        return null;
    }
}