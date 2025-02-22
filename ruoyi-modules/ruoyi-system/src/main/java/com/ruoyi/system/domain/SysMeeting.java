package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 系统会议对象 sys_meeting
 *
 * @author dmzhang
 * @date 2025-02-22
 */
public class SysMeeting extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 会议id
     */
    @Excel(name = "会议id")
    private String meetingId;

    /**
     * 会议议题
     */
    @Excel(name = "会议议题")
    private String title;

    /**
     * 会议密码
     */
    @Excel(name = "会议密码")
    private String password;

    /**
     * 会议发起人
     */
    private Long holdUserId;

    /**
     * 会议状态
     */
    @Excel(name = "会议状态")
    private Long status;

    /**
     * 计划开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "计划开始", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date planStartTime;

    /**
     * 计划结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "计划结束", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date planEndTime;

    /**
     * 实际开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "实际开始", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date actStartTime;

    /**
     * 实际结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "实际结束", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date actEndTime;

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setHoldUserId(Long holdUserId) {
        this.holdUserId = holdUserId;
    }

    public Long getHoldUserId() {
        return holdUserId;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Date getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setActStartTime(Date actStartTime) {
        this.actStartTime = actStartTime;
    }

    public Date getActStartTime() {
        return actStartTime;
    }

    public void setActEndTime(Date actEndTime) {
        this.actEndTime = actEndTime;
    }

    public Date getActEndTime() {
        return actEndTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("meetingId", getMeetingId())
                .append("title", getTitle())
                .append("password", getPassword())
                .append("holdUserId", getHoldUserId())
                .append("status", getStatus())
                .append("planStartTime", getPlanStartTime())
                .append("planEndTime", getPlanEndTime())
                .append("actStartTime", getActStartTime())
                .append("actEndTime", getActEndTime())
                .toString();
    }
}
