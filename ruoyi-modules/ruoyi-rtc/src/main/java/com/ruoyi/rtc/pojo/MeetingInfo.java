package com.ruoyi.rtc.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author dz
 */
public class MeetingInfo implements Serializable{

    /**
     *  会议发起者 userId
     */
    private Long meetingOwnerUserId;
    /**
     *  会议发起者 sessionId
     */
    private String meetingOwnerSessionId;
    /**
     *  会议 Id
     */
    private String meetingId;
    /**
     * 会议标题
     */
    private String meetingName;
    /**
     *  会议密码
     */
    private String meetingPassword;
    /**
     *  会议当前在线成员
     */
    private List<MeetingMember> meetingMembers;

    public Long getMeetingOwnerUserId() {
        return meetingOwnerUserId;
    }

    public void setMeetingOwnerUserId(Long meetingOwnerUserId) {
        this.meetingOwnerUserId = meetingOwnerUserId;
    }

    public String getMeetingOwnerSessionId() {
        return meetingOwnerSessionId;
    }

    public void setMeetingOwnerSessionId(String meetingOwnerSessionId) {
        this.meetingOwnerSessionId = meetingOwnerSessionId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingPassword() {
        return meetingPassword;
    }

    public void setMeetingPassword(String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }

    public List<MeetingMember> getMeetingMembers() {
        return meetingMembers;
    }

    public void setMeetingMembers(List<MeetingMember> meetingMembers) {
        this.meetingMembers = meetingMembers;
    }

    static class MeetingMember implements Serializable {
        /**
         *  会议成员 userId
         */
        private Long userId;
        /**
         * 会议成员 sessionId
         */
        private String sessionId;
        /**
         *  会议成员姓名
         */
        private String userName;
        /**
         *  会议成员票据信息
         */
        private String ticket;
        /**
         *  会议成员头像信息
         */
        private String avatar;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getTicket() {
            return ticket;
        }

        public void setTicket(String ticket) {
            this.ticket = ticket;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
