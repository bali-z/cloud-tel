package com.ruoyi.rtc.pojo;

import java.io.Serializable;

/**
 * offer 信令实体
 *
 * @author dz
 */
public class OfferMessage extends BaseMessage implements Serializable {

    private OfferData data;

    public OfferData getData() {
        return data;
    }

    public void setData(OfferData data) {
        this.data = data;
    }

    static class OfferData implements Serializable {
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
