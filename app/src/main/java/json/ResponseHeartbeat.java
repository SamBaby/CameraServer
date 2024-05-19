package json;

import java.util.List;

public class ResponseHeartbeat {

    private String info;

    private List<SerialData> serialData;

    private String shutoff;

    private String snapnow;

    private int isUpdate;

    private String upFileUrl;

    private ShowPlayQRCode showPlayQRCode;

    private AudioPlay audioPlay;

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

    public String getShutoff() {
        return shutoff;
    }

    public void setShutoff(String shutoff) {
        this.shutoff = shutoff;
    }

    public String getSnapnow() {
        return snapnow;
    }

    public void setSnapnow(String snapnow) {
        this.snapnow = snapnow;
    }

    public int getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(int isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getUpFileUrl() {
        return upFileUrl;
    }

    public void setUpFileUrl(String upFileUrl) {
        this.upFileUrl = upFileUrl;
    }

    public ShowPlayQRCode getShowPlayQRCode() {
        return showPlayQRCode;
    }

    public void setShowPlayQRCode(ShowPlayQRCode showPlayQRCode) {
        this.showPlayQRCode = showPlayQRCode;
    }

    public AudioPlay getAudioPlay() {
        return audioPlay;
    }

    public void setAudioPlay(AudioPlay audioPlay) {
        this.audioPlay = audioPlay;
    }

    // Nested class for 'showPlayQRCode'
    public static class ShowPlayQRCode {

        private int enable;

        private int urlMode;

        private String url;

        private int scond;

        // Getters and setters
        public int getEnable() {
            return enable;
        }

        public void setEnable(int enable) {
            this.enable = enable;
        }

        public int getUrlMode() {
            return urlMode;
        }

        public void setUrlMode(int urlMode) {
            this.urlMode = urlMode;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getScond() {
            return scond;
        }

        public void setScond(int scond) {
            this.scond = scond;
        }
    }

    // Nested class for 'audioPlay'
    public static class AudioPlay {

        private int audioMode;

        private int fee;

        private String plate;

        private int totaltime;

        private int playMode;

        private int voiceSpeed;

        // Getters and setters
        public int getAudioMode() {
            return audioMode;
        }

        public void setAudioMode(int audioMode) {
            this.audioMode = audioMode;
        }

        public int getFee() {
            return fee;
        }

        public void setFee(int fee) {
            this.fee = fee;
        }

        public String getPlate() {
            return plate;
        }

        public void setPlate(String plate) {
            this.plate = plate;
        }

        public int getTotaltime() {
            return totaltime;
        }

        public void setTotaltime(int totaltime) {
            this.totaltime = totaltime;
        }

        public int getPlayMode() {
            return playMode;
        }

        public void setPlayMode(int playMode) {
            this.playMode = playMode;
        }

        public int getVoiceSpeed() {
            return voiceSpeed;
        }

        public void setVoiceSpeed(int voiceSpeed) {
            this.voiceSpeed = voiceSpeed;
        }
    }
}