package com.example.cameraserver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import Util.ApacheServerReqeust;
import Util.UtilString;
import datamodel.Cam;
import datamodel.CarHistory;
import datamodel.CarInside;
import event.Var;
import json.AlarmInfoPlate;
import json.Heartbeat;
import json.ResponseAlarmInfoPlate;
import json.SerialData;

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
//            try {
//                HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8001), 0);
//                server.createContext("/", new MyHandler());
////                server.setExecutor(null); // creates a default executor
//                server.start();
//            } catch (Exception e) {
//                Log.e("SUNServer", e.toString());
//            }

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

    private void handleClient(Socket socket) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if (socket.isConnected()) {
                String line;
                boolean catchMode = false;
//                String ret = checkCanOut(new AlarmInfoPlate());
                // Send response back to client
                try {
                    String carNo = port.getText().toString();
                    CarInside carInside = new CarInside();
                    carInside.setCar_number(carNo);
                    carInside.setColor("white");
                    carInside.setType("Large");
                    carInside.setGate("A");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    carInside.setTime_in(format.format(date));
                    carInside.setPicture_url(UtilString.sampleImage);
                    Thread t = new Thread(() -> {
                        ApacheServerReqeust.addCarInside(carInside);
                    });
                    t.start();
                    t.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.write((getHTTPResponseOK() + "{\"dsad\"}").getBytes());
                output.flush();
                // Close streams and socket
                output.close();
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
                    JsonObject ret = new JsonObject();
                    ret.add(UtilString.CameraString.Response_AlarmInfoPlate, gson.toJsonTree(res));
                    return gson.toJson(ret);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private String checkCar(AlarmInfoPlate info) {
        Var<String> ret = new Var<>("");
        String ip = info.getIpaddr();

        //check cam is in or out : false-in, true-out
        boolean inOut = checkGateType(ip);
        if (inOut) {//out
            ret.set(checkCanOut(info));
        } else {//in
            ret.set(checkCanIn(info));
        }
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
        return "";
    }

    private String checkCanOut(AlarmInfoPlate info) {
//        String carNo = info.getResult().getPlateResult().getLicense();
        String carNo = "567JQA";
        Var<CarInside> car = new Var<>();
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
        return "";
    }


    private String checkHeartbeat(Heartbeat info) {
        return null;
    }

    private String checkSerialData(SerialData info) {
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

            String response = checkType(byteArrayOutputStream.toString());
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
