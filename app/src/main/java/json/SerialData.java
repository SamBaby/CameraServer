package json;


public class SerialData {

    private int channel;

    private String serialno;

    private String ipaddr;

    private int serialChannel;

    private String data;

    private int dataLen;

    // Getters and setters
    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public int getSerialChannel() {
        return serialChannel;
    }

    public void setSerialChannel(int serialChannel) {
        this.serialChannel = serialChannel;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDataLen() {
        return dataLen;
    }

    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }
}