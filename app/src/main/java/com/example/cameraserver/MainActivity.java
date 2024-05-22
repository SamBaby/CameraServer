package com.example.cameraserver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import datamodel.Cam;
import datamodel.CarHistory;
import datamodel.CarInside;
import event.Var;
import json.AlarmGioIn;
import json.AlarmInfoPlate;
import json.Heartbeat;
import json.ResponseAlarmInfoPlate;
import json.SerialData;
import util.ApacheServerReqeust;
import util.Util;
import util.UtilString;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    TextView port;
    TextView msg_car;
    private String connectIp;
    Map<Integer, String> colorMap = new HashMap<>();
    Map<Integer, String> sizeMap = new HashMap<>();

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
        setColorMap();
        setSizeMap();
        port = findViewById(R.id.port);
        port.setText("567JQA");
        image = findViewById(R.id.image_car);
        msg_car = findViewById(R.id.text_car);
        Button connect = findViewById(R.id.connect);
        Button disconnect = findViewById(R.id.disconnect);
//        try {
//            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8081), 0);
//            server.createContext("/", new MyHandler());
////                server.setExecutor(null); // creates a default executor
//            server.start();
//        } catch (Exception e) {
//            Log.e("SUNServer", e.toString());
//        }
        new Thread(() -> {
            try {
                // Create server socket
                server = new ServerSocket(8081);

                while (true) {
                    // Accept incoming connections
                    Socket socket = server.accept();

                    // Handle client request in a separate thread
                    new Thread(() -> {
                        handleClient(socket);
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        connect.setOnClickListener(v -> {
            if (server != null) {
                try {
                    server.close();
                    server = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

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

    private void setSizeMap() {
        sizeMap.put(0, "unknown");
        sizeMap.put(1, "大");
        sizeMap.put(2, "中");
        sizeMap.put(3, "小");
    }

    private void setColorMap() {
        colorMap.put(0, "unknown");
        colorMap.put(1, "black");
        colorMap.put(2, "white");
        colorMap.put(3, "deep red");
        colorMap.put(4, "red");
        colorMap.put(5, "dark yellow");
        colorMap.put(6, "yellow");
        colorMap.put(7, "dark gray");
        colorMap.put(8, "gray");
        colorMap.put(9, "dark blue");
        colorMap.put(10, "blue");
        colorMap.put(11, "dark green");
        colorMap.put(12, "green");
        colorMap.put(13, "dark pink");
        colorMap.put(14, "pink");
        colorMap.put(15, "dark brown");
        colorMap.put(16, "brown");
        colorMap.put(17, "dark purple");
        colorMap.put(18, "purple");
    }

    private static String TAG = "ServerSocketTest";

    private ServerSocket server;
    private Map<String, Boolean> camQueueDetect = new HashMap<>();
    private Map<String, Boolean> camInDetect = new HashMap<>();
    private Var<AlarmInfoPlate> carInfo = new Var<>();

    private void handleClient(Socket socket) {
        try {
            if (socket.isConnected()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                boolean catchMode = false;
//                String ret = checkCanOut(new AlarmInfoPlate());
                // Send response back to client
                try {
                    StringBuilder builder = new StringBuilder();
                    boolean startParsing = false;
                    int stop = 1;
                    int bytesRead;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // 读取客户端发送的数据
                    while (((bytesRead = br.read()) != -1) || (stop <= 0)) {
                        char c = (char) bytesRead;
                        if (c == '{') {
                            startParsing = true;
                        } else if (c == '}') {
                            stop--;
                        }
                        builder.append(c);
                    }
                    try {
                        JSONObject obj = new JSONObject(builder.toString());
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String res = "";
                        if (obj.has(UtilString.CameraString.AlarmInfoPlate)) {
                            AlarmInfoPlate info = gson.fromJson(obj.toString(), AlarmInfoPlate.class);
                            res = checkCar(info);
                        } else if (obj.has(UtilString.CameraString.heartbeat)) {
                            Heartbeat heartbeat = gson.fromJson(obj.toString(), Heartbeat.class);
                            res = checkHeartbeat(heartbeat);
                        } else if (obj.has(UtilString.CameraString.SerialData)) {
                            SerialData data = gson.fromJson(obj.toString(), SerialData.class);
                            res = checkSerialData(data);
                        } else if (obj.has(UtilString.CameraString.AlarmGioIn)) {
                            AlarmGioIn data = gson.fromJson(obj.toString(), AlarmGioIn.class);
                            checkAlarmGoIn(data);
                        }

                        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                        output.write((getHTTPResponseOK() + res).getBytes());
                        output.flush();
                        output.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Close streams and socket
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getHTTPResponseOK() {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n";
    }

    private static String getHTTPResponseNo() {
        return "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "400 Bad Request";
    }

    private String checkCar(AlarmInfoPlate info) {
        Var<String> ret = new Var<>("");
        String ip = info.getIpaddr();
        byte[] decodedBytes = Util.getBase64Decode(info.getResult().getPlateResult().getImageFile());
        runOnUiThread(()->{
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            image.setImageBitmap(bitmap);
            ret.set(getCameraResponse(true));
        });
        //check cam is in or out : false-in, true-out
//        boolean inOut = checkGateType(ip);
//        if (inOut) {//out
//            ret.set(checkCanOut(info));
//        } else {//in
//            ret.set(checkCanIn(info));
//        }
        return ret.get();
    }

    private boolean checkGateType(String ip) {
        Var<Boolean> inOrOut = new Var<>(false);
        Thread t = new Thread(() -> {
            String res = ApacheServerReqeust.getCam(ip);
            if (res != null && !res.isEmpty()) {
                try {
                    JSONArray array = new JSONArray(res);
                    if (array.length() > 0) {
                        JSONObject obj = array.getJSONObject(0);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        Cam cam = gson.fromJson(obj.toString(), Cam.class);
                        int inOut = cam.getIn_out();
                        inOrOut.set(inOut == 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inOrOut.get();
    }

    private boolean checkGateOpen(AlarmInfoPlate info) {
        return false;
    }

    private String checkCanIn(AlarmInfoPlate info) {
        String carNo = info.getResult().getPlateResult().getLicense();
        int color = info.getResult().getPlateResult().getCarColor();
        int type = info.getResult().getPlateResult().getType();
        int size = info.getResult().getPlateResult().getVehicleSize();
        String imageString = info.getResult().getPlateResult().getImageFile();
        int imageLength = info.getResult().getPlateResult().getImageFileLen();
        Var<Boolean> open = new Var<>(false);
        Thread checkCar = new Thread(() -> {
            int total = 0;
            try {
                String slot = ApacheServerReqeust.getLeftLot();
                if (slot != null && !slot.isEmpty()) {
                    JSONObject obj = null;
                    obj = new JSONArray(slot).getJSONObject(0);
                    total += obj.getInt("car_slot");
                    total += obj.getInt("pregnant_slot");
                    total += obj.getInt("disabled_slot");
                    total += obj.getInt("charging_slot");
                    total += obj.getInt("reserved_slot");
                }
                String cars = ApacheServerReqeust.getCarInsideCount();
                if (cars != null && !cars.isEmpty()) {
                    JSONObject obj = new JSONArray(cars).getJSONObject(0);
                    total -= obj.getInt("COUNT(*)");
                }
                //available slot
                if (total > 0) {
                    CarInside carInside = new CarInside();
                    carInside.setCar_number(carNo);
                    carInside.setColor("white");
                    carInside.setType("Large");
                    carInside.setGate("A");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    carInside.setTime_in(format.format(date));
                    carInside.setPicture_url(imageString);
                    ApacheServerReqeust.addCarInside(carInside);
                    open.set(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            checkCar.start();
            checkCar.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getCameraResponse(open.get());
    }

    private String checkCanOut(AlarmInfoPlate info) {
        String carNo = info.getResult().getPlateResult().getLicense();
        Var<CarInside> car = new Var<>();
        Var<Boolean> open = new Var<>(false);
        Thread getCar = new Thread(() -> {
            String res = ApacheServerReqeust.getCarInsideWithCarNumber(carNo);
            if (res != null && !res.isEmpty()) {
                try {
                    JSONArray array = new JSONArray(res);
                    if (array.length() > 0) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        CarInside carInside = gson.fromJson(array.getJSONObject(0).toString(), CarInside.class);
                        car.set(carInside);
                        String time_pay = car.get().getTime_pay();
                        if (time_pay != null && !time_pay.isEmpty()) {
                            try {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                long now = new Date().getTime();
                                long pay = format.parse(time_pay).getTime();
                                long diff = now - pay;
                                long diffMinutes = diff / (60 * 1000);
                                if (diffMinutes <= 15) {
                                    //delete cars_inside data
                                    ApacheServerReqeust.deleteCarInside(carNo);
                                    //add car history data
                                    CarHistory history = new CarHistory();
                                    history.setCar_number(carNo);
                                    history.setTime_in(car.get().getTime_in());
                                    history.setTime_out(format.format(pay));
                                    history.setTime_pay(car.get().getTime_pay());
                                    history.setCost(car.get().getCost());
                                    history.setBill_number(car.get().getBill_number());
                                    history.setPayment(car.get().getPayment());
                                    history.setArtificial(car.get().getArtificial());
                                    history.setType(car.get().getType());
                                    history.setColor(car.get().getColor());
                                    ApacheServerReqeust.addHistory(history);
                                    //return camera response and open gate
                                    open.set(true);
                                } else {
                                    //camara response and close gate
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            getCar.start();
            getCar.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getCameraResponse(open.get());
    }

    private String getCameraResponse(boolean openGate) {
        ResponseAlarmInfoPlate res = new ResponseAlarmInfoPlate();
        res.setContent("retransfer_stop");
        res.setIs_pay("true");
        res.setSerialData(new ArrayList<>());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject ret = new JsonObject();
        ret.add(UtilString.CameraString.Response_AlarmInfoPlate, gson.toJsonTree(res));
        if (openGate) {
            res.setInfo("ok");
        } else {
            res.setInfo("no");
        }
        return gson.toJson(ret);
    }

    private String checkHeartbeat(Heartbeat info) {
        return "";
    }

    private String checkSerialData(SerialData info) {
        return "";
    }

    /***
     *
     * @param info  IO port changes info
     * @return
     */
    private void checkAlarmGoIn(AlarmGioIn info) {
        String ip = info.getIpaddr();
        int source = info.getResult().getTriggerResult().getSource();
        int value = info.getResult().getTriggerResult().getValue();
        if (source == 0) {
            if (value == 0) {
                camQueueDetect.put(ip, true);
            } else {
                camQueueDetect.put(ip, false);
            }
        } else {
            if (value == 0) {
                camInDetect.put(ip, true);
            } else {
                camInDetect.put(ip, false);
            }
        }
        runOnUiThread(()->{
            msg_car.setText(String.format("source %d - value %d", source, value));
        });
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

            String response = "";
            t.sendResponseHeaders(200, response.length());
            t.getResponseHeaders().set("Content-Type", "application/json");
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String cameraContent() {
            return null;
        }
    }
}
