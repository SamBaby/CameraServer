package json;

import com.google.gson.annotations.SerializedName;

public class AlarmInfoPlate {
    private int channel;
    private String deviceName;
    private String ipaddr;
    private Result result;
    private String serialno;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

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

    public static class Result {
        @SerializedName("PlateResult")
        private PlateResult plateResult;

        public PlateResult getPlateResult() {
            return plateResult;
        }

        public void setPlateResult(PlateResult plateResult) {
            this.plateResult = plateResult;
        }
    }

    public static class PlateResult {
        private int bright;
        private int carBright;
        private int carColor;
        private int vehicleBrand;
        private int vehicleSize;
        private int colorType;
        private int colorValue;
        private int confidence;
        private int direction;
        private String imageFile;
        private int imageFileLen;
        private String imageFragmentFile;
        private int imageFragmentFileLen;
        private String license;
        private Location location;
        private TimeStamp timeStamp;
        private int timeUsed;
        private int triggerType;
        private int type;

        public int getBright() {
            return bright;
        }

        public void setBright(int bright) {
            this.bright = bright;
        }

        // Other getters and setters for the remaining fields
    }

    public static class Location {
        private Rect RECT;

        public Rect getRECT() {
            return RECT;
        }

        public void setRECT(Rect RECT) {
            this.RECT = RECT;
        }
    }

    public static class Rect {
        private int top;
        private int left;
        private int right;
        private int bottom;

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        // Other getters and setters for the remaining fields
    }

    public static class TimeStamp {
        private Timeval Timeval;

        public AlarmInfoPlate.TimeStamp.Timeval getTimeval() {
            return Timeval;
        }

        public void setTimeval(AlarmInfoPlate.TimeStamp.Timeval timeval) {
            Timeval = timeval;
        }

        public static class Timeval {
            private long sec;
            private long usec;

            public long getSec() {
                return sec;
            }

            public void setSec(long sec) {
                this.sec = sec;
            }

            // Other getters and setters for the remaining fields
        }
    }
}