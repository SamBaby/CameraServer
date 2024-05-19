package json;

public class AlarmGioIn {

    private String deviceName;

    private String ipaddr;

    private Result result;

    private String serialno;

    // Getters and setters
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    // Nested class for 'result'
    public static class Result {

        private TriggerResult TriggerResult;

        // Getters and setters
        public TriggerResult getTriggerResult() {
            return TriggerResult;
        }

        public void setTriggerResult(TriggerResult triggerResult) {
            this.TriggerResult = triggerResult;
        }
    }

    // Nested class for 'TriggerResult'
    public static class TriggerResult {

        private int source;

        private int value;

        // Getters and setters
        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
