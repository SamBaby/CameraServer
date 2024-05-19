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

        public int getCarBright() {
            return carBright;
        }

        public void setCarBright(int carBright) {
            this.carBright = carBright;
        }

        public int getCarColor() {
            return carColor;
        }

        public void setCarColor(int carColor) {
            this.carColor = carColor;
        }

        public int getVehicleBrand() {
            return vehicleBrand;
        }

        public void setVehicleBrand(int vehicleBrand) {
            this.vehicleBrand = vehicleBrand;
        }

        public int getVehicleSize() {
            return vehicleSize;
        }

        public void setVehicleSize(int vehicleSize) {
            this.vehicleSize = vehicleSize;
        }

        public int getColorType() {
            return colorType;
        }

        public void setColorType(int colorType) {
            this.colorType = colorType;
        }

        public int getColorValue() {
            return colorValue;
        }

        public void setColorValue(int colorValue) {
            this.colorValue = colorValue;
        }

        public int getConfidence() {
            return confidence;
        }

        public void setConfidence(int confidence) {
            this.confidence = confidence;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public String getImageFile() {
            return imageFile;
        }

        public void setImageFile(String imageFile) {
            this.imageFile = imageFile;
        }

        public int getImageFileLen() {
            return imageFileLen;
        }

        public void setImageFileLen(int imageFileLen) {
            this.imageFileLen = imageFileLen;
        }

        public String getImageFragmentFile() {
            return imageFragmentFile;
        }

        public void setImageFragmentFile(String imageFragmentFile) {
            this.imageFragmentFile = imageFragmentFile;
        }

        public int getImageFragmentFileLen() {
            return imageFragmentFileLen;
        }

        public void setImageFragmentFileLen(int imageFragmentFileLen) {
            this.imageFragmentFileLen = imageFragmentFileLen;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public TimeStamp getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(TimeStamp timeStamp) {
            this.timeStamp = timeStamp;
        }

        public int getTimeUsed() {
            return timeUsed;
        }

        public void setTimeUsed(int timeUsed) {
            this.timeUsed = timeUsed;
        }

        public int getTriggerType() {
            return triggerType;
        }

        public void setTriggerType(int triggerType) {
            this.triggerType = triggerType;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
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

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }

        public int getBottom() {
            return bottom;
        }

        public void setBottom(int bottom) {
            this.bottom = bottom;
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

            public long getUsec() {
                return usec;
            }

            public void setUsec(long usec) {
                this.usec = usec;
            }

            // Other getters and setters for the remaining fields
        }
    }
}