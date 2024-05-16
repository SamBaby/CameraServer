package json;

import java.util.List;

public class ResponseAlarmInfoPlate {
    private String info;
    private String content;
    private String is_pay;
    private List<SerialData> serialData;
    private ShowPlayQRCode showPlayQRCode;
    private AudioPlay audioPlay;
    private ShowDataInfo showDataInfo;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIs_pay() {
        return is_pay;
    }

    public void setIs_pay(String is_pay) {
        this.is_pay = is_pay;
    }

    public List<SerialData> getSerialData() {
        return serialData;
    }

    public void setSerialData(List<SerialData> serialData) {
        this.serialData = serialData;
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

    public ShowDataInfo getShowDataInfo() {
        return showDataInfo;
    }

    public void setShowDataInfo(ShowDataInfo showDataInfo) {
        this.showDataInfo = showDataInfo;
    }

    public static class SerialData {
        private int serialChannel;
        private String data;
        private int dataLen;

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

    public static class ShowPlayQRCode {
        private int enable;
        private int urlMode;
        private String url;
        private int scond;

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

    public static class AudioPlay {
        private int audioMode;
        private int fee;
        private String plate;
        private int totaltime;
        private int playMode;
        private int voiceSpeed;

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

    public static class ShowDataInfo {
        private int scond;
        private List<LineInfo> lineInfo;

        public int getScond() {
            return scond;
        }

        public void setScond(int scond) {
            this.scond = scond;
        }

        public List<LineInfo> getLineInfo() {
            return lineInfo;
        }

        public void setLineInfo(List<LineInfo> lineInfo) {
            this.lineInfo = lineInfo;
        }
    }

    public static class LineInfo {
        private int line;
        private int fontcolor;
        private int fontsize;
        private String lcdcontent;

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public int getFontcolor() {
            return fontcolor;
        }

        public void setFontcolor(int fontcolor) {
            this.fontcolor = fontcolor;
        }

        public int getFontsize() {
            return fontsize;
        }

        public void setFontsize(int fontsize) {
            this.fontsize = fontsize;
        }

        public String getLcdcontent() {
            return lcdcontent;
        }

        public void setLcdcontent(String lcdcontent) {
            this.lcdcontent = lcdcontent;
        }
    }
}