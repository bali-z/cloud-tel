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
     *  会议发起者 userId (获取票据时填充)
     */
    private Long meetingOwnerUserId;
    /**
     *  会议发起者 sessionId (会议发起者 enter时填充)
     */
    private String meetingOwnerSessionId;
    /**
     *  会议 Id (获取票据时填充)
     */
    private String meetingId;
    /**
     * 会议标题 (获取票据时填充)
     */
    private String meetingTitle;
    /**
     *  会议密码 (获取票据时填充)
     */
    private String meetingPassword;
    /**
     *  会议开始时间 (获取票据时填充)
     */
    private Date startTime;
    /**
     *  会议结束时间 (获取票据时填充)
     */
    private Date endTime;

    /**
     *  会议发起人 姓名(会议发起者enter时填充)
     */
    private String meetingOwnerName;

    /**
     *  会议发起人头像 (会议发起者enter时填充)
     */
    private String meetingOwnerAvatar;
    /**
     *  会议当前在线成员 (成员enter时添加)
     */
    private List<MeetingMember> meetingMembers;

}
