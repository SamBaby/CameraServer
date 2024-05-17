package bean;


import java.io.Serializable;

import cn.com.brilliants.sdk.face.messageclient.bean.Packet;

/**
 * tcp接收到的数据
 */
public class TcpMessage implements Serializable {
    private int code;//消息分类  99-地址解析异常  100-连接成功 200-收到设备发来的数据 201-连接失败 202-断开连接
    private String connectTag;//服务标识
    private String errHint;//出现异常时的说明  code!=200时
    private Packet packet;//接收到的包信息


    public TcpMessage(String tag,int code, String errHint) {
        this.connectTag=tag;
        this.code = code;
        this.errHint = errHint;
    }

    public TcpMessage(int code, String connectTag,  Packet packet,String errHint) {
        this.code = code;
        this.connectTag = connectTag;
        this.errHint = errHint;
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public String getErrHint() {
        return errHint;
    }

    public void setErrHint(String errHint) {
        this.errHint = errHint;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getConnectTag() {
        return connectTag;
    }

    public void setConnectTag(String connectTag) {
        this.connectTag = connectTag;
    }


}
