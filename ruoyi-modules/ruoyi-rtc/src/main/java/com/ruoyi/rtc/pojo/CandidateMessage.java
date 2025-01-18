package com.ruoyi.rtc.pojo;

import java.io.Serializable;

/**
 * candidate 信令
 * @author dz
 */
public class CandidateMessage extends BaseMessage implements Serializable {

    private CandidateData candidateData;

    public CandidateData getCandidateData() {
        return candidateData;
    }

    public void setCandidateData(CandidateData candidateData) {
        this.candidateData = candidateData;
    }

    static class CandidateData implements Serializable {
        /**
         *  表示候选者的 SDP 信息属性字符串，描述了该候选者的详细信息。
         */
        private String candidate;

        /**
         * 表示候选者的媒体流 ID，用于标识候选者关联的媒体流。
         */
        private String sdpMLineIndex;

        /**
         * 表示候选者的媒体流 ID，用于标识候选者关联的媒体流。
         */
        private String sdpMid;

        /**
         * 表示候选者的用户名片段，用于标识候选者关联的媒体流。
         */
        private String usernameFragment;

        public String getCandidate() {
            return candidate;
        }

        public void setCandidate(String candidate) {
            this.candidate = candidate;
        }

        public String getSdpMLineIndex() {
            return sdpMLineIndex;
        }

        public void setSdpMLineIndex(String sdpMLineIndex) {
            this.sdpMLineIndex = sdpMLineIndex;
        }

        public String getSdpMid() {
            return sdpMid;
        }

        public void setSdpMid(String sdpMid) {
            this.sdpMid = sdpMid;
        }

        public String getUsernameFragment() {
            return usernameFragment;
        }

        public void setUsernameFragment(String usernameFragment) {
            this.usernameFragment = usernameFragment;
        }
    }
}
