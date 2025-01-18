package com.ruoyi.rtc.pojo;

import java.io.Serializable;

/**
 * Answer 信令
 * @author dz
 */
public class AnswerMessage extends BaseMessage implements Serializable{
    private AnswerData data;

    public AnswerData getData() {
        return data;
    }

    public void setData(AnswerData data) {
        this.data = data;
    }

    static class AnswerData implements Serializable {
        /**
         * 会话描述协议 (Session Description Protocol)，用于描述多媒体通信的参数。
         */
        private String sdpInfo;
        /**
         * 信令
         */
        private String signal;

        public String getSdpInfo() {
            return sdpInfo;
        }

        public void setSdpInfo(String sdpInfo) {
            this.sdpInfo = sdpInfo;
        }

        public String getSignal() {
            return signal;
        }

        public void setSignal(String signal) {
            this.signal = signal;
        }
    }
}
