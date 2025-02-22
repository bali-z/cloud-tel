package com.ruoyi.rtc.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * enter 信令后有
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
public class MeetingMember implements Serializable {
    /**
     * 会议成员 userId
     */
    private Long userId;
    /**
     * 会议成员 sessionId
     */
    private String sessionId;
    /**
     * 会议成员姓名
     */
    private String name;
    /**
     * 会议成员票据信息
     */
    private String ticket;
    /**
     * 会议成员头像信息
     */
    private String avatar;
    /**
     * 会议用户权限(麦克风，视频，录屏) 0|1|0 视频 可以
     */
    private String permission;

}