package com.ruoyi.common.core.pojo;

import lombok.Data;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 *  会议进程对象
 */
@SuppressWarnings("ALL")
@Data
public class MeetingProcess {

    /**
     *  会议ID (用户获取ticket时就有了)
     */
    private String meetingId;
    /**
     *  会议发起人ID (用户获取ticket时就有了)
     */
    private Long holdUserId;
    /**
     *  会议发起人姓名 (用户获取ticket时就有了)
     */
    private String name;
    /**
     *  会议发起人头像 (用户获取ticket时就有了)
     */
    private String avatarUrl;
    /**
     *  会议发起人sessionId (用户发起者 ENTER后有)
     */
    private String holdUserSessionId;
    /**
     *  会议计划开始时间
     */
    private Date planStartTime;
    /**
     *  会议计划结束时间
     */
    private Date planEndTime;

    /**
     *  会议成员
     */
    private Set<MeetingMember> meetingMembers;

    /**
     *  会议成员
     */
    @Data
    public static class MeetingMember{
        /**
         * 成员USERID (ENTER 后有)
         */
        private Long userId;
        /**
         *  成员姓名 (ENTER 后有)
         */
        private String name;
        /**
         *  成员头像 (ENTER 后有)
         */
        private String avatarUrl;
        /**
         *  成员sessionId (ENTER 后有)
         */
        private String sessionId;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MeetingMember that = (MeetingMember) o;
            return userId.equals(that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId);
        }
    }
}
