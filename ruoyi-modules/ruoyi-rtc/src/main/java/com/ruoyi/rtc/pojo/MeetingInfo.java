package com.ruoyi.rtc.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * joinMeeting 亲求后有
 * @author dz
 */
@Data
@Accessors(chain = true)
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
    private String meetingTitle;
    /**
     *  会议密码
     */
    private String meetingPassword;
    /**
     *  会议开始时间
     */
    private Date startTime;
    /**
     *  会议结束时间
     */
    private Date endTime;

    /**
     *  会议发起人 姓名
     */
    private String meetingOwnerName;

    /**
     *  会议发起人头像
     */
    private String meetingOwnerAvatar;
    /**
     *  会议当前在线成员
     */
    private List<MeetingMember> meetingMembers;

    /**
     *  Join 信令后有
     */
    @Data
    @Accessors(chain = true)
    public static class MeetingMember implements Serializable {
        /**
         *
         *  会议成员 token
         */
        private String userToken;

        /**
         *
         *  会议成员 userId
         */
        private Long userId;
        /**
         *
         * 会议成员 sessionId
         */
        private String sessionId;
        /**
         *  会议成员姓名
         */
        private String name;
        /**
         *  会议成员票据信息
         */
        private String ticket;
        /**
         *  会议成员头像信息
         */
        private String avatar;
        /**
         *  会议用户权限(麦克风，视频，录屏) 0|1|0 视频 可以
         */
        private String permission;

    }
}
