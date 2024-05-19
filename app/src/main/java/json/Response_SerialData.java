package json;

import java.util.List;

public class Response_SerialData {

    private String info;

    private List<SerialData> serialData;

    // Getters and setters
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<SerialData> getSerialData() {
        return serialData;
    }

    public void setSerialData(List<SerialData> serialData) {
        this.serialData = serialData;
    }
}