package com.ruoyi.rtc.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 会议管理对象 sys_meeting_data
 * 
 * @author dz
 * @date 2025-01-18
 */
public class SysMeeting extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 会议唯一id */
    private String meetingId;

    /** 会议标题 */
    @Excel(name = "会议标题")
    private String title;

    /** 会议发起用户 */
    private Long userId;

    /** 会议密码，密文 */
    private String password;

    /** 会议开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "会议开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 会议结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "会议结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 非参会人员是否限制参会 */
    @Excel(name = "非参会人员是否限制参会")
    private Character isRestricted;

    /** 参会人员 userId , 拼接 */
    private String attendees;

    @Excel(name = "参会人员")
    private String attendeesName;

    private List<Long> attendeesUserIdList;

    public List<Long> getAttendeesUserIdList() {
        return attendeesUserIdList;
    }

    public void setAttendeesUserIdList(List<Long> attendeesUserIdList) {
        this.attendeesUserIdList = attendeesUserIdList;
    }

    public String getAttendeesName() {
        return attendeesName;
    }

    public void setAttendeesName(String attendeesName) {
        this.attendeesName = attendeesName;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setMeetingId(String meetingId) 
    {
        this.meetingId = meetingId;
    }

    public String getMeetingId() 
    {
        return meetingId;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setIsRestricted(Character isRestricted)
    {
        this.isRestricted = isRestricted;
    }

    public Character getIsRestricted()
    {
        return isRestricted;
    }
    public void setAttendees(String attendees) 
    {
        this.attendees = attendees;
    }

    public String getAttendees() 
    {
        return attendees;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("meetingId", getMeetingId())
            .append("title", getTitle())
            .append("userId", getUserId())
            .append("password", getPassword())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("isRestricted", getIsRestricted())
            .append("attendees", getAttendees())
            .append("createTime", getCreateTime())
            .toString();
    }
}
