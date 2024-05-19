package json;


public class Heartbeat {

    private int countid;

    private TimeStamp timeStamp;

    private String serialno;

    // Getters and setters
    public int getCountid() {
        return countid;
    }

    public void setCountid(int countid) {
        this.countid = countid;
    }

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    // Nested class for 'timeStamp'
    public static class TimeStamp {

        private Timeval Timeval;

        // Getters and setters
        public Timeval getTimeval() {
            return Timeval;
        }

        public void setTimeval(Timeval timeval) {
            Timeval = timeval;
        }
    }

    // Nested class for 'Timeval'
    public static class Timeval {

        private long sec;

        private long usec;

        // Getters and setters
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
    }
}